package com.yulissa.takeawayapp.util;

import com.yulissa.takeawayapp.constant.ConstantNames;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于未登录情况下临时访问微服务
 * @author yulissa
 */
public class TempTokenGenerator {
    /**
     * 生成临时令牌用于不需要登录的操作,如注册
     * @param request
     * @param signer
     * @param ttl 单位为分钟
     * @return
     */
    public static String generate(HttpServletRequest request, Signer signer, int ttl) {
        JsonParser objectMapper = JsonParserFactory.create();
        Long exp = System.currentTimeMillis() + 60L * ttl;
        Map<String, Object> map = new HashMap<>();
        map.put("user_name", "anonymous");
        map.put("exp", exp);
        map.put("authorities", "ROLE_anonymous");
        map.put("client_id", "microservices");
        map.put("scope", "app");
        map.put("IP", RealIPExtractor.getRealIp(request));
        String content = objectMapper.formatMap(map);
        return ConstantNames.TOKEN_NAME+ "=" + JwtHelper.encode(content, signer).getEncoded();
    }
}
