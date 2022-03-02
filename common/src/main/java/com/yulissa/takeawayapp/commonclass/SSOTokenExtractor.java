package com.yulissa.takeawayapp.commonclass;

import com.yulissa.takeawayapp.constant.ConstantNames;
import com.yulissa.takeawayapp.util.RealIPExtractor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * @author yulissa
 */
public class SSOTokenExtractor implements TokenExtractor {

    private JsonParser objectMapper = JsonParserFactory.create();

    KeyPair keyPair;

    public SSOTokenExtractor(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @SneakyThrows
    @Override
    public Authentication extract(HttpServletRequest request) {
        String tokenValue = "";
        if (request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ConstantNames.TOKEN_NAME)) {
                    tokenValue = cookie.getValue();
                    break;
                }
            }
        }

        if (!tokenValue.equals("")) {
            Jwt jwt = JwtHelper.decodeAndVerify(tokenValue, new RsaVerifier((RSAPublicKey)keyPair.getPublic()));
            String claimsStr = jwt.getClaims();
            Map<String, Object> claims = objectMapper.parseMap(claimsStr);
            String recordedIP = (String)claims.get("IP");
            String userIP = RealIPExtractor.getRealIp(request);
            // 防止盗取token冒充用户
            if (recordedIP != null) {
                if (userIP == null || !userIP.equals(recordedIP)) {
                    return null;
                }
            }
            request.getSession().setAttribute("username", claims.get("user_name"));
            request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, OAuth2AccessToken.BEARER_TYPE);
            return new PreAuthenticatedAuthenticationToken(tokenValue, "");
        } else {
            return null;
        }
    }
}
