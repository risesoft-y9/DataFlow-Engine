package net.risedata.rpc.consumer.annotation;

import org.springframework.stereotype.Indexed;

import net.risedata.rpc.consumer.invoke.InvokeHandle;

import java.lang.annotation.*;

/**
 * 放到方法上提示该方法为api
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Target(ElementType.METHOD)
public @interface API {
    /**
     * 接口别名 默认是方法名 rpcName+"/"+name=mapping 映射
     *
     * @return
     */
    String name() default "";

    /**
     * 超时时间 当 为 负数的时候则 不会使用此超时时间
     * 如果不需要超时时间 则设置为 0
     */
    int timeOut() default -1;

    /**
     * 此api 对应的执行invoke 的handle
     * 此handle 必须在spring 工厂中
     * @return invoke handle
     */
    Class<? extends InvokeHandle> invokeHandle() default InvokeHandle.class;


}
