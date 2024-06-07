package net.risedata.jdbc.annotations.repository;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在dynamic dao 上添加此注解说明用于修改(增,删,改)操作
 * 
 * @author libo 2021年7月8日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Modify {
	String value();
}
