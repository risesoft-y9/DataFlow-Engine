package net.risedata.rpc.provide.annotation;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;


/**
 * @description: 是否启动扫描 RequestParam 注解
 * @Author lb176
 * @Date 2021/4/29==16:27
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Target(ElementType.TYPE)
public @interface EnableRequestParam {
}
