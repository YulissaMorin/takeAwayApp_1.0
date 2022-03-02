package com.yulissa.takeawayapp.constant;

import java.lang.reflect.Field;

/**
 * @author yulissa
 */
public class Urls {
    public static final String DOMAIN = "takeawayapp.com";

    public static class NoAuthUrls {
        public static final String LOGIN = "/user_account/login";
        public static final String REGISTER = "/user_account/register";
        public static final String HOMEPAGE = "/user_account/homepage";
        public static final String GET_CAPTCHA = "/user_account/getCaptcha";
        public static final String REGISTER_CONFIRM_PREFIX = "/user_account/register_confirm";
        public static final String RESET_PASSWORD_PREFIX = "/user_account/reset_password";

        public static final String SIGNIN = "/app-user/sign_in";

        public static String[] getNoAuthUrlsForConfig() throws IllegalAccessException {
            Field[] fields = NoAuthUrls.class.getFields();
            String[] noAuthUrls = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                String value = (String) fields[i].get(null);
                if (fields[i].getName().endsWith("PREFIX")) {
                    value += "/**";
                }
                noAuthUrls[i] = value;
            }
            return noAuthUrls;
        }
    }
}
