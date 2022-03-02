package com.yulissa.takeawayapp.handler;

import com.yulissa.takeawayapp.constant.ConstantNames;
import com.yulissa.takeawayapp.util.CookieUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yulissa
 */
public class OauthFailHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        CookieUtil.addCookie(response, "takeawayapp.com", "/", "username", "", 0, true);
        CookieUtil.addCookie(response, "takeawayapp.com", "/", "uid", "", 0, true);
        CookieUtil.addCookie(response, "takeawayapp.com", "/", ConstantNames.TOKEN_NAME,
                "", 0, true
        );
        response.sendRedirect("http://useroauth.takeawayapp.com/user_account/login");
    }
}
