package net.risedata.jdbc.annotations.update;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 增加此注解的字段会在update中作为id加入where 条件
 * 
 * @author libo 2020年10月21日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface UpdateId {
	/**
	 * 是否在 wehre 的同时进行update操作 default false
	 * 
	 * @return
	 */
	boolean isUpdate() default false;

	/**
	 * 是否占位开启后 如果没有传入自定义的操作则不会生效 只在操作为update的时候生效
	 */
	boolean isPlaceholder() default false;
}
