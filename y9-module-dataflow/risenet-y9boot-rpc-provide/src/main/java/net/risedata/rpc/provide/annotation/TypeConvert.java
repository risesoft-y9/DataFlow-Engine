package net.risedata.rpc.provide.annotation;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;


/**
* @description: 声明自己是处理typeConvert的
* @Author lb176
* @Date 2021/4/29==16:28
*/
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Component
@Target(ElementType.TYPE)
public @interface TypeConvert {
    //添加到哪些服务api中 如果没有则为全部
    String[] value() default {};

}
