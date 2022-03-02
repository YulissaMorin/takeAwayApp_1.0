package com.yulissa.takeawayapp.util;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author yulissa
 */
public class TokenDecoder {
    // 因为AccessTokenConverter的decode方法是protected,所以这里不能注入
    // 只能自己用jwthelper解析
    private static final String publickey = "-----BEGIN PUBLIC KEY-----MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3Ir04L0OyVmq7B1SYzhr3hjBNYcUKou3OfqhxrIB5ZA5gURcvgv7d5AOcPhrirIHaEzhQA5Fwdqp+Oap2G/ey1Iboqg1cjiB6o5u2wMFM8/33LDs7jU7bnk+ha1nHQcuEovsjaWI4i0FME8u8U/rrLMbXHjwaoyQyR9fBku/GWQIDAQAB-----END PUBLIC KEY-----";

    public static Map<String,String> decodeToken(String token) {
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));
        String claims = jwt.getClaims();
        return JSON.parseObject(claims,Map.class);

    }

    @Test
    void test() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NDQ0NDQzODcsInVzZXJfbmFtZSI6Ijk0NDQxNjAyMEBxcS5jb20iLCJhdXRob3JpdGllcyI6WyJST0xFX3VzZXIiXSwiY2xpZW50X2lkIjoibWljcm9zZXJ2aWNlcyIsInNjb3BlIjpbImFwcCJdLCJJUCI6IjE5Mi4xNjguMzEuMTAxIn0.VkpGNxWYkGrEDiqIqQa1bRsFz6rp_RcjnL7qco61XJ7aMb24Pe2N0p1K1IXL7rWCICSaUAvJbzFmg09qELJrf0-6y_NYzHNSAGFlHByUGyCLRBor-8t0mNKxiAE_SNDrWSkSJQgouYuKwNlaoo0_-qkNbiNtl34UebnsP7XWVkg";
        Map<String,String> map = decodeToken(token);
        Date date = new Date(1644241387L);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(date));
        System.out.println(map);
    }


}
