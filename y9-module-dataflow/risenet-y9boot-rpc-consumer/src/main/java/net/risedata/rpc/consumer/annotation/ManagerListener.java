package net.risedata.rpc.consumer.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Indexed;

/**
 * @description: 监听器所监听的连接管理器的名字
 * @Author lb176
 * @Date 2021/4/29==16:22
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Target(ElementType.TYPE)
public @interface ManagerListener {

    /**
     * 监听管理器的名字
     * 
     * @return
     */
    String[] name() default {};

    /**
     * 监听的连接管理器的class
     */
    Class<?>[] classes() default {};

}
