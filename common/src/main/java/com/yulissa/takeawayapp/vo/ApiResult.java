package com.yulissa.takeawayapp.vo;

import com.yulissa.takeawayapp.constant.StatusCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yulissa
 */
@Data
public class ApiResult<T> implements Serializable {

    private boolean flag;
    private String statusCode;
    private String message;
    private T data;


    public ApiResult<T> apiResult(boolean flag, String statusCode, String message,T data) {
        this.flag = flag;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        return this;
    }

    public ApiResult<T> successApiResult() {
        this.flag = true;
        this.statusCode = StatusCode.OK;
        this.message = "";
        return this;
    }

    public ApiResult<T> successApiResult(String message) {
        this.flag = true;
        this.statusCode = StatusCode.OK;
        this.message = message;
        return this;
    }

    public ApiResult<T> successApiResult(T data) {
        this.flag = true;
        this.statusCode = StatusCode.OK;
        this.message = "";
        this.data = data;
        return this;
    }

    public ApiResult<T> successApiResult(T data, String message) {
        this.flag = true;
        this.statusCode = StatusCode.OK;
        this.message = message;
        this.data = data;
        return this;
    }

    public ApiResult<T> failApiResult(String statusCode, String message) {
        this.flag = false;
        this.statusCode = statusCode;
        this.message = message;
        return this;
    }

}
