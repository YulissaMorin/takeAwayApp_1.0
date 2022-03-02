package com.yulissa.takeawayapp.aopannotation;

import java.lang.annotation.*;

/**
 * @author yulissa
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface MethodTimer {

}
