package net.risedata.rpc.consumer.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Indexed;

import net.risedata.rpc.consumer.listener.ListenerBack;

/**
 * @Description : 此注解表示这是一个监听器
 * @ClassName Listener
 * @Author lb
 * @Date 2021/11/24 14:26
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Target(ElementType.METHOD)
public @interface Listener {
    /**
     * 事件名字
     * 
     * @return
     */
    String value();

    /**
     * 当事件回调出现异常时
     * 
     * @return
     */
    Class<ListenerBack> backError() default ListenerBack.class;

}
