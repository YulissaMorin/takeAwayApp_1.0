package com.yulissa.takeawayapp.exception;

/**
 * @author yulissa
 * 项目暂时所有未catch的exception都为GlobalException,待细分
 */
public class GlobalException extends RuntimeException {
    public GlobalException(String message) {
        super(message);
    }
    public GlobalException() {
        super();
    }
}
