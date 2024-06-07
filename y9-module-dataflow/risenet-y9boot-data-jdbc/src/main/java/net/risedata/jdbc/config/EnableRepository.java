package net.risedata.jdbc.config;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 需要被load的包 注意无需写** 只需写需要被扫描的包名
 * 
 * @author libo 2020年7月15日
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableRepository {
	String[] value() default { "" };
}
