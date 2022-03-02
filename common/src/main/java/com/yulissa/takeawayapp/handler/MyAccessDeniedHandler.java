package com.yulissa.takeawayapp.handler;

import com.yulissa.takeawayapp.constant.ConstantNames;
import com.yulissa.takeawayapp.util.CookieUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yulissa
 */
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        CookieUtil.addCookie(response, "takeawayapp.com", "/", "username", "", 0, true);
        CookieUtil.addCookie(response, "takeawayapp.com", "/", "uid", "", 0, true);
        CookieUtil.addCookie(response, "takeawayapp.com", "/", ConstantNames.TOKEN_NAME,
                "", 0, true
        );
        response.sendRedirect("http://useroauth.takeawayapp.com/user_account/login");
    }
}
