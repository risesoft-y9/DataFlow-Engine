package net.risedata.rpc.provide.annotation;

import java.lang.annotation.*;
/**
* @description: 对于参数描述的注解
* @Author lb176
* @Date 2021/4/29==16:27
*/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    boolean required() default true;

    String defaultValue() default "";

}
