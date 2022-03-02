package com.yulissa.takeawayapp.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author yulissa
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.error(Arrays.toString(ex.getStackTrace()));
        ModelAndView errorPage = new ModelAndView();
        errorPage.addObject("message", "出现未知错误,抱歉");
        errorPage.setViewName("fail");
        return errorPage;
    }
}
