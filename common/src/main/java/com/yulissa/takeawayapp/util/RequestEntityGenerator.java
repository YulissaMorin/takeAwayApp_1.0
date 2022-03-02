package com.yulissa.takeawayapp.util;

import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author yulissa
 */
public class RequestEntityGenerator {
    Object body = new LinkedMultiValueMap<>();
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    public RequestEntityGenerator addBody(String key, Object value) {
        if (body instanceof MultiValueMap) {
            ((MultiValueMap<String, Object>) body).add(key, value);
        }
        return this;
    }

    public RequestEntityGenerator addHeader(String key, String value) {
        headers.add(key, value);
        return this;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public HttpEntity generate() {
        return new HttpEntity(body, headers);
    }

}
