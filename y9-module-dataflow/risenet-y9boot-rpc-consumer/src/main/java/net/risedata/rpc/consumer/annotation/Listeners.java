package net.risedata.rpc.consumer.annotation;

import org.springframework.stereotype.Indexed;

import net.risedata.rpc.consumer.listener.ListenerBack;

import java.lang.annotation.*;

/**
 * @description: 添加此注解说明此类里面的方法会作用于监听器
 * @Author lb176
 * @Date 2021/4/29==16:22
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Target(ElementType.TYPE)
public @interface Listeners {

    /**
     * 当事件回调出现异常时 TODO 考虑在之后版本推行暂时不支持
     * @return
     */
    Class<ListenerBack> backError() default ListenerBack.class;

}
