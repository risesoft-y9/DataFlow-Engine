package net.risedata.rpc.provide.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
