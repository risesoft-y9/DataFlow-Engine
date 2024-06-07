package net.risedata.rpc.provide.annotation;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
* @description: 添加了此注解的 类会被视为rpc调用的服务类
* @Author lb176
* @Date 2021/4/29==16:28
*/
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Target(ElementType.TYPE)
public @interface RPCServer {
    /**
     * 给此api服务起名字
     * @return 名字
     */
   String name() default "";
    /**
     * 是否获取request对象request是此rpc框架的request非httprequest
     * @return
     */
    boolean enableRequest() default false;
}
