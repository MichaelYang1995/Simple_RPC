package com.pq.rpc.autoconfig.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: RPCReference
 * @Description: 服务引用注解类
 *               添加@RPCReference的字段将被注入一个对应服务的实现类的代理Bean中
 * @Author: YangQingyuan
 * @Date: 2019/12/2 11:23
 * @Version: 1.0
 */
@Target(ElementType.FIELD)  //该注解作用于字段上
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCReference {
    boolean async() default false;

    boolean callback() default false;

    boolean oneWay() default false;

    //超时时间
    long timeout() default 3000;

    //回调方法
    String callbackMethod() default "";

    int callbackParamIndex() default 1;
}
