package net.risedata.rpc.consumer.annotation;

import org.springframework.stereotype.Indexed;

import net.risedata.rpc.consumer.listener.ExceptionListener;

import java.lang.annotation.*;

/**
 * @description: 客户端api注解
 * @Author lb176
 * @Date 2021/4/29==16:22
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Target(ElementType.TYPE)
public @interface RPCClinet {
    /**
     * 配置提供此服务的服务端ip 端口
     * 注意: 可以是纯ip 端口的 则为 rpc:127.0.0.1:8888
     * 可以是SpringCloud整合的则为 cloud:service-name
     *
     * @return 连接的url
     */
    String[] value() default {};

    /**
     * api的名字 默认为空
     *
     * @return 名字
     */
    String name() default "";

    /**
     * 超时时间 当 为 负数的时候则 不会使用此超时时间
     * 如果不需要超时时间 则设置为 0
     */
    int timeOut() default -1;

    /**
     * 出现异常 所调用的 异常处理类
     * 注意: 在没有增加超时熔断的时候
     * 超时也会调用 这个异常如果不想处理超时的异常可以进行判断
     *
     * @return 异常的类
     */
    Class<? extends ExceptionListener> exceptionHandle() default ExceptionListener.class;

    /**
     * 降级机制 必须是实现了此api 的接口的类当然也可以是接口
     * 接口必须在spring 中有实现类
     * 如果是类  请不要在类的头上增加 spring 的扫描注解
     *
     * @return 熔断类
     */
    Class<?> degrade() default ExceptionListener.class;

    /**
     * 使用的connection manager 的名字
     *
     * @return 连接管理器名字
     */
    String managerName() default "";



}
