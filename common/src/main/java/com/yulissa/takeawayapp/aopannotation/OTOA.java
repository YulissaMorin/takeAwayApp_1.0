package com.yulissa.takeawayapp.aopannotation;

import java.lang.annotation.*;

/**
 * @author yulissa
 * OTOA为operate their own account的缩写
 * 检查用户是否正在操作自己的资源,是否获取了权限却用来操作不属于自己的资源
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface OTOA {

}
