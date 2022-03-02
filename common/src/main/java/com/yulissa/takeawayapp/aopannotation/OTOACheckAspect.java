package com.yulissa.takeawayapp.aopannotation;

import com.yulissa.takeawayapp.commonmethod.CommonMethods;
import com.yulissa.takeawayapp.exception.IllegalRequestException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yulissa
 * OTOA为operate their own account的缩写
 * 检查用户是否正在操作自己的资源,是否获取了权限却用来操作不属于自己的资源
 */
@Aspect
@Component
@Slf4j
public class OTOACheckAspect {
    @Pointcut("@annotation(com.yulissa.takeawayapp.aopannotation.OTOA)")
    private void method(){}

    @Before("method()")
    public void doBefore() {
        String currentUser = CommonMethods.getUsernameFromContext();
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String requestedResourceUsername = request.getParameter("username");
        if (requestedResourceUsername != null) {
            if (!requestedResourceUsername.equals(currentUser)) {
                throw new IllegalRequestException("非法资源请求");
            }
        }
    }
}
