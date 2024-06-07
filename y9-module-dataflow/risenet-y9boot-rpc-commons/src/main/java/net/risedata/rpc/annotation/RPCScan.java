package net.risedata.rpc.annotation;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * 设置扫描的包路径
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Target(ElementType.TYPE)
public @interface RPCScan {
    String[] value();

}
