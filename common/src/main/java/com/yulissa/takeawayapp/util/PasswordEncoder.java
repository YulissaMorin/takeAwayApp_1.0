package com.yulissa.takeawayapp.util;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author yulissa
 */
public class PasswordEncoder {

    @Test
    public void testTest() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode("666666");
        System.out.println(password);
    }
}
