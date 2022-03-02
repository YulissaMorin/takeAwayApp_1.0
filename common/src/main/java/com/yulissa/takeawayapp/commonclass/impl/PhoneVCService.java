package com.yulissa.takeawayapp.commonclass.impl;

import com.yulissa.takeawayapp.commonclass.interfaces.VCService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 待完善,暂未购买语音验证码服务
 * @author yulissa
 */
public class PhoneVCService implements VCService {
    @Override
    public String saveAndSendVC(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return null;
    }

    @Override
    public String getStoredVC(HttpServletRequest request, String username) {
        return null;
    }
}
