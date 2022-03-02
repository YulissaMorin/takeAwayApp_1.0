package com.yulissa.takeawayapp.aopannotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author yulissa
 */
@Aspect
@Component
@Slf4j
public class MethodTimerAspect {
    @Pointcut("@annotation(com.yulissa.takeawayapp.aopannotation.MethodTimer)")
    private void method(){}

    @Around("method()")
    public void logTime(ProceedingJoinPoint pj) throws Throwable {
        long start = System.currentTimeMillis();
        pj.proceed();
        long end = System.currentTimeMillis();
        float spend = (end - start)/1000F;
        String info = pj.getSignature().getDeclaringTypeName()+" 的 "+pj.getSignature().getName()+" 方法用时"+spend + "秒";
        // info级别的信息太多了,为方便起见本项目开发与生产环境均使用warn来记录debug信息,实际项目中应使用debug或info级别
        log.warn(info);
    }
}
