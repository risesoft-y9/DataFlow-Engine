package net.risedata.jdbc.annotations.order;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 升序排序
 * @author libo
 *2020年10月15日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(Ascs.class)
public @interface Asc {
	/**
	 * 排序级别
	 * @return
	 */
    int leve() default 0;
    
    /**
     * 表达式什么情况下生效
     * @return
     */
    String expression() default "";
}
