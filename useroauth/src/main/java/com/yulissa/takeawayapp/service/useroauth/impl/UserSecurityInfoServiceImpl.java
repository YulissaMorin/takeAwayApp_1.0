package com.yulissa.takeawayapp.service.useroauth.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yulissa.takeawayapp.aopannotation.OTOA;
import com.yulissa.takeawayapp.commonclass.interfaces.EmailService;
import com.yulissa.takeawayapp.commonclass.interfaces.VCService;
import com.yulissa.takeawayapp.commonmethod.CommonMethods;
import com.yulissa.takeawayapp.constant.ConstantNames;
import com.yulissa.takeawayapp.constant.StatusCode;
import com.yulissa.takeawayapp.constant.SupportedVCMethods;
import com.yulissa.takeawayapp.constant.Urls;
import com.yulissa.takeawayapp.dao.useroauth.UserSecurityInfoMapper;
import com.yulissa.takeawayapp.entity.useroauth.UserSecurityInfo;
import com.yulissa.takeawayapp.service.useroauth.UserSecurityInfoService;
import com.yulissa.takeawayapp.util.CookieUtil;
import com.yulissa.takeawayapp.util.RequestEntityGenerator;
import com.yulissa.takeawayapp.util.TempTokenGenerator;
import com.yulissa.takeawayapp.util.UrlGenerator;
import com.yulissa.takeawayapp.vo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.datasource.DataSourceException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author yulissa
 * @since 2022-01-29
 */
@Slf4j
@Service
public class UserSecurityInfoServiceImpl extends ServiceImpl<UserSecurityInfoMapper, UserSecurityInfo> implements UserSecurityInfoService {

    @Autowired
    Map<String, VCService> VCServices;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    EmailService emailService;

    @Autowired
    KeyPair keyPair;

    @Bean
    Signer signer() {
        return new RsaSigner((RSAPrivateKey) keyPair.getPrivate());
    }

    @Autowired
    Signer signer;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${oauth.clientId}")
    private String clientId;

    @Value("${oauth.clientSecret}")
    private String clientSecret;

    @Override
    public ApiResult register(UserSecurityInfo userSecurityInfo, HttpServletRequest request) throws Exception {
        boolean flag = VCServices.get(SupportedVCMethods.CAPTCHA).verifyVC(request, null,request.getParameter("captcha"));
        if (!flag) {
            return new ApiResult().failApiResult(StatusCode.ABNORMAL_REGISTER, "???????????????,???????????????");
        }

        String emailCode = UUID.randomUUID().toString();

        UserSecurityInfo storedSecurityInfo = findStoredUserInfo(userSecurityInfo.getUsername());
        if (storedSecurityInfo != null) {
            return processExistUser(storedSecurityInfo, emailCode);
        }

        insertUser(userSecurityInfo);
        String encodedUsername = Base64Utils.encodeToString(userSecurityInfo.getUsername().getBytes());
        try {
            String confirmLink = UrlGenerator.generateRest("useroauth",Urls.DOMAIN, null, Urls.NoAuthUrls.REGISTER_CONFIRM_PREFIX, encodedUsername, emailCode);
            emailService.sendEmail(userSecurityInfo.getUsername(), "???????????????????????????", confirmLink);
        } catch (Exception e) {
            return new ApiResult().failApiResult(StatusCode.SYSTEM_ERROR, "????????????????????????,???????????????");
        }
        stringRedisTemplate.boundValueOps(encodedUsername).set(emailCode, 30, TimeUnit.MINUTES);
        return new ApiResult().successApiResult("????????????,???????????????,????????????????????????????????????,?????????????????????(??????????????????????????????????????????QAQ ????????????)");
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response) {
        String username = CommonMethods.getUsernameFromContext();
        if (StringUtils.isEmpty(username)) {throw new IllegalArgumentException("????????????,??????????????????????????????????????????.???????????????");}
        QueryWrapper<UserSecurityInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        baseMapper.delete(wrapper);
        logout(request, response);
        String token = TempTokenGenerator.generate(request, signer, 5);
        HttpEntity entity =
                new RequestEntityGenerator()
                        .addBody("username", username)
                        .addHeader("Cookie", token).generate();
        restTemplate.postForObject("http://user/app-user/delete", entity, Integer.class);
        return username;
    }

    @Override
    public ApiResult deleteByUsername(String username) {
        return null;
    }

    @Override
    public ApiResult registerConfirm(String encodedUsername, String emailCode) throws Exception {
        UserSecurityInfo userSecurityInfo = verifyVCAndEnableUser(encodedUsername, emailCode);
        if (userSecurityInfo == null) {return new ApiResult().failApiResult(StatusCode.ABNORMAL_REGISTER, "??????????????????,????????????????????????");}
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = TempTokenGenerator.generate(request, signer, 5);
        HttpEntity entity =
                new RequestEntityGenerator()
                        .addBody("uid", String.valueOf(userSecurityInfo.getUid()))
                        .addBody("email", userSecurityInfo.getUsername())
                        .addHeader("Cookie", token)
                        .generate();
        try {
            restTemplate.postForObject("http://user/app-user/add", entity, Integer.class);
        } catch (Exception e) {
            log.error("rpc error" + Arrays.toString(e.getStackTrace()));
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("email", userSecurityInfo.getUsername());
                map.put("uid", userSecurityInfo.getUid());
                rabbitTemplate.convertAndSend("register_backup_queue",JSON.toJSONString(map));
            } catch (Exception ex) {
                return new ApiResult().failApiResult(StatusCode.SYSTEM_ERROR, "??????????????????,?????????????????????");
            }
            return new ApiResult().failApiResult(StatusCode.RPC_ERROR, "?????????????????????????????????,???????????????,?????????????????????????????????????????????");
        }
        return new ApiResult().successApiResult("??????????????????!???????????????!");
    }

    @Override
    public IPage<UserSecurityInfo> findListByPage(Integer page, Integer pageCount) {
        return null;
    }

    @Override
    public ApiResult login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        String url = "http://useroauth/oauth/token";
        HttpEntity entity =
                new RequestEntityGenerator()
                        .addBody("client_id", clientId)
                        .addBody("client_secret", clientSecret)
                        .addBody("username", username)
                        .addBody("password", password)
                        .addBody("grant_type", "password")
                        .addBody("scope", "app")
                        .generate();
        Map map = null;
        try {
            map = restTemplate.postForObject(url, entity, Map.class);
        } catch (Exception e) {
            return new ApiResult().failApiResult(StatusCode.ABNORMAL_LOGIN, "????????????????????????,???????????????");
        }
        CookieUtil.addCookie(response, "takeawayapp.com", "/", "username",
                username, 259000, true
        );
        CookieUtil.addCookie(response, "takeawayapp.com", "/", ConstantNames.TOKEN_NAME,
                (String) map.get("access_token"), 259000, true
        );
        UserSecurityInfo user = findStoredUserInfo(username);
        CookieUtil.addCookie(response, "takeawayapp.com", "/", "uid",
                user.getUid().toString(), 2590000, true
        );
        return new ApiResult().successApiResult();
    }

    @Override
    public void logout(HttpServletRequest request,HttpServletResponse response) {
        request.getSession().setAttribute("username", "");
        CookieUtil.addCookie(response, "takeawayapp.com", "/", "username", "", 0, true);
        CookieUtil.addCookie(response, "takeawayapp.com", "/", "uid", "", 0, true);
        CookieUtil.addCookie(response, "takeawayapp.com", "/", ConstantNames.TOKEN_NAME,
                "", 0, true
        );
    }

    @OTOA
    @Override
    public ApiResult updateData(UserSecurityInfo userSecurityInfo) {
        baseMapper.updateById(userSecurityInfo);
        return new ApiResult().successApiResult("????????????!");
    }

    @Override
    public UserSecurityInfo findById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        VCServices.get(SupportedVCMethods.CAPTCHA).saveAndSendVC(request, response);
    }

    @Override
    public ApiResult resetPasswordStep1(String email) {
        UserSecurityInfo user = findStoredUserInfo(email);
        if (user == null) {return new ApiResult().failApiResult(StatusCode.ABNORMAL_REQUEST, "????????????????????????,???????????????");}
        String emailCode = UUID.randomUUID().toString();
        stringRedisTemplate.boundValueOps(String.valueOf(user.getUid())).set(emailCode);
        String passwordResetLink = UrlGenerator.generateRest("useroauth",Urls.DOMAIN, null, Urls.NoAuthUrls.RESET_PASSWORD_PREFIX,"step2",user.getUid().toString(), emailCode);
        emailService.sendEmail(email, "???????????????????????????",
                "??????????????????: " + passwordResetLink + "\n" + "???????????????????????????????????????,30???????????????"
        );
        return new ApiResult().successApiResult("??????????????????");
    }

    @Override
    public ApiResult resetPasswordStep2(long uid, String emailCode, String password) {
        String storedEmailCode = stringRedisTemplate.opsForValue().get(String.valueOf(uid));
        if (!emailCode.equals(storedEmailCode)) {return new ApiResult().failApiResult(StatusCode.ABNORMAL_REQUEST, "??????????????????????????????");}
        UserSecurityInfo userSecurityInfo = new UserSecurityInfo();
        userSecurityInfo.setUid(uid);
        userSecurityInfo.setPassword(passwordEncoder.encode(password));
        baseMapper.updateById(userSecurityInfo);
        return new ApiResult().successApiResult("????????????!");
    }

    private ApiResult processExistUser(UserSecurityInfo storedSecurityInfo, String emailCode) {
        if (storedSecurityInfo.getEnabled()) {
            return new ApiResult().failApiResult(StatusCode.ABNORMAL_REGISTER, "??????????????????,?????????????????????????????????");
        } else {
            String encodedUsername = Base64Utils.encodeToString(storedSecurityInfo.getUsername().getBytes());
            String confirmLink = UrlGenerator.generateRest("useroauth",Urls.DOMAIN, null, Urls.NoAuthUrls.REGISTER_CONFIRM_PREFIX, encodedUsername, emailCode);
            try {
                stringRedisTemplate.boundValueOps(encodedUsername).set(emailCode, 30, TimeUnit.MINUTES);
                emailService.sendEmail(storedSecurityInfo.getUsername(), "???????????????????????????", confirmLink);
            } catch (Exception e) {
                return new ApiResult().failApiResult(StatusCode.RPC_ERROR, "??????????????????????????????,??????????????????,???????????????");
            }
            return new ApiResult().successApiResult("??????????????????????????????,?????????????????????,?????????");
        }
    }

    private void insertUser(UserSecurityInfo userSecurityInfo) {
        Long uid = IdWorker.getId();
        userSecurityInfo.setUid(uid);
        userSecurityInfo.setPassword(passwordEncoder.encode(userSecurityInfo.getPassword()));
        userSecurityInfo.setAuthorities("ROLE_user");
        userSecurityInfo.setEnabled(false);
        baseMapper.insert(userSecurityInfo);
    }

    private UserSecurityInfo verifyVCAndEnableUser(String encodedUsername, String emailCode) throws Exception {
        String storedEmailCode = stringRedisTemplate.boundValueOps(encodedUsername).get();
        if (storedEmailCode == null || !storedEmailCode.equals(emailCode)) {
            return null;
        }
        UserSecurityInfo userSecurityInfo = findStoredUserInfo(new String(Base64Utils.decodeFromString(encodedUsername) ) );
        if (userSecurityInfo == null) {
            return null;
        }
        userSecurityInfo.setEnabled(true);
        baseMapper.updateById(userSecurityInfo);
        return userSecurityInfo;
    }

     @Override
     public ApiResult sendRegisterInfoToMq(String encodedUsername, String emailCode) throws Exception {
        UserSecurityInfo userSecurityInfo = verifyVCAndEnableUser(encodedUsername, emailCode);
        if (userSecurityInfo == null) {return new ApiResult().failApiResult(StatusCode.ABNORMAL_REGISTER, "??????????????????,????????????????????????");}
        Map<String, Object> map = new HashMap();
        map.put("email", userSecurityInfo.getUsername());
        map.put("uid", userSecurityInfo.getUid());
        try {
            rabbitTemplate.convertAndSend(JSON.toJSONString(map));
        } catch (Exception e) {
            return new ApiResult().failApiResult(StatusCode.SYSTEM_ERROR, "??????????????????,???????????????");
        }
        return new ApiResult().successApiResult("????????????,??????????????????,?????????????????????????????????");
    }

    private UserSecurityInfo findStoredUserInfo(String username) throws DataSourceException {
        QueryWrapper<UserSecurityInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return baseMapper.selectOne(wrapper);
    }
}
