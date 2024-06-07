package net.risedata.jdbc.config;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 需要被load的包 只需写包名
 * 
 * @author libo 2020年7月15日
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JdbcScan {
	String[] value() default { "" };
}
