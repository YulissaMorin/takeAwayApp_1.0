package com.yulissa.takeawayapp.util;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author yulissa
 */
public class UrlGenerator {
    public static String generateRest(String secondLevelDomain,String domain, String port, String path, String ... pathVariables) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(secondLevelDomain)) {sb.append(secondLevelDomain).append(".");}
        sb.append(domain);
        if (!StringUtils.isEmpty(port)) {sb.append(":").append(port);}
        sb.append(path);
        for (String pathVariable : pathVariables) {
            sb.append("/").append(pathVariable);
        }
        return sb.toString();
    }

    public static String generateQuery(String domain, String port, String path, Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append(domain);
        if (!"".equals(port)) {sb.append(":").append(port);}
        sb.append(path).append("?");
        for (Map.Entry entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
