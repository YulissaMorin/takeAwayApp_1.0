package com.yulissa.takeawayapp.commonclass.impl;

import com.yulissa.takeawayapp.commonclass.interfaces.EmailService;
import com.yulissa.takeawayapp.commonclass.interfaces.VCService;
import com.yulissa.takeawayapp.commonmethod.CommonMethods;
import com.yulissa.takeawayapp.exception.IllegalRequestException;
import io.lettuce.core.RedisException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 只适用于发送单纯验证码,若发送包含其他参数的URL不要使用此方法
 * @author yulissa
 */

@Component
public class EmailVCService implements VCService {

    @Autowired(required = false)
    StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    EmailService emailService;

    @Override
    public String saveAndSendVC(HttpServletRequest request, HttpServletResponse response) throws RedisException {
        String emailCode = UUID.randomUUID().toString();
        String username = "";
        if (!StringUtils.isEmpty(request.getParameter("username"))) {
            username = request.getParameter("username");
        } else {
            username = CommonMethods.getUsernameFromContext();
        }
        if (username.equals("")) {throw new IllegalRequestException("登录过期,请重新登录后再操作");}
        username = Base64Utils.encodeToString(username.getBytes());
        stringRedisTemplate.boundValueOps(username).set(emailCode, 30, TimeUnit.MINUTES);
        request.setAttribute("encodedUsername",username);
        emailService.sendEmail(username, "饱了吗验证码", emailCode);
        return emailCode;
    }

    @Override
    public String getStoredVC(HttpServletRequest request, String encodedUsername) {
        return stringRedisTemplate.opsForValue().get(encodedUsername);
    }

}
