package net.risedata.jdbc.annotations.mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 增加的此注解的字段会添加自定义的映射操作
 * 
 * @author libo 2021年2月9日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ColMapping {
	/**
	 * 对应的类 类获取方式参考 SpringApplicationFactory
	 * 
	 * @return
	 */
	Class<? extends net.risedata.jdbc.mapping.ColumnMapping> value();

	/**
	 * 调用构建方法传入的参数
	 * 
	 * @return
	 */
	String[] args() default {};

}
