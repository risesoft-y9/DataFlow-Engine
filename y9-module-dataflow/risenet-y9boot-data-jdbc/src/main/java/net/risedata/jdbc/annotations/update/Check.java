package net.risedata.jdbc.annotations.update;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 添加了此注解的字段会在 upadte or insert 的时候生效 
 * 会先查库检查是否存在 用的是 等于操作
 * @author libo
 *2020年11月4日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Check {
	
}
