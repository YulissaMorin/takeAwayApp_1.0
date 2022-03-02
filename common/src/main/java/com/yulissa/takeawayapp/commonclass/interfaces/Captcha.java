package com.yulissa.takeawayapp.commonclass.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yulissa
 */
public interface Captcha {
    /**
     * 获取验证码
     * @return
     * @param request
     * @param response
     */
    void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
