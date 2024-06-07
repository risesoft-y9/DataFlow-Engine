package net.risedata.rpc.provide.annotation;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;


/**
* @description:添加在方法上表示此方法是api服务
* @Author lb176
* @Date 2021/8/25==14:46
*/
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Target(ElementType.METHOD)
public @interface API {
    /**
     * 接口别名 默认是方法名
     * @return
     */
    String name() default "";

    /**
     * 超时时间 当 为 负数的时候则 不会使用此超时时间
     * 如果不需要超时时间 则设置为 0
     */
    int timeOut() default -1;



}
