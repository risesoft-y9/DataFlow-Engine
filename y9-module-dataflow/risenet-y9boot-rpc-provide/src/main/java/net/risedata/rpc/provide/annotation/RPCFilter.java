package net.risedata.rpc.provide.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Indexed;

/**
 * @Description : 配置过滤器的一些信息
 * @ClassName RPCFilter
 * @Author lb
 * @Date 2021/11/24 10:05
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Target(ElementType.TYPE)
public @interface RPCFilter {
    /**
     * 那些url被匹配
     * 
     * @return
     */
    String value() default "**";

    /**
     * 不被匹配的url
     */
    String noMatcher() default "**";
}
