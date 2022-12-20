package com.xxxx.seckill.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：注解化实现接口限流
 * Author: asus
 * Date: 2022/12/20 12:02
 */
@Retention(RetentionPolicy.RUNTIME)//注解是运行时生效的
@Target(ElementType.METHOD)//用于方法
public @interface AccessLimit {

    int second();

    int maxCount();

    boolean needLogin() default true;
}
