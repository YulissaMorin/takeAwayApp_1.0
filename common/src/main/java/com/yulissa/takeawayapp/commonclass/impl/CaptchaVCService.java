package com.yulissa.takeawayapp.commonclass.impl;

import com.google.code.kaptcha.Producer;
import com.yulissa.takeawayapp.commonclass.interfaces.VCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author yulissa
 */
@Component
public class CaptchaVCService implements VCService {

    @Autowired
    Producer producer;

    @Override
    public String saveAndSendVC(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        String code = producer.createText();
        BufferedImage image = producer.createImage(code);
        // 保存验证码到session
        request.getSession().setAttribute("captcha", code);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        return code;
    }

    @Override
    public String getStoredVC(HttpServletRequest request, String username) {
        if (request.getSession(false) != null) {
            return (String) request.getSession().getAttribute("captcha");
        } else {
            return "";
        }
    }


}
