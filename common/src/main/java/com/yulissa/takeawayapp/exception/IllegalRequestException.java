package com.yulissa.takeawayapp.exception;

/**
 * @author yulissa
 */
public class IllegalRequestException extends RuntimeException{
    public IllegalRequestException(String message) {
        super(message);
    }
    public IllegalRequestException() {
        super();
    }
}
