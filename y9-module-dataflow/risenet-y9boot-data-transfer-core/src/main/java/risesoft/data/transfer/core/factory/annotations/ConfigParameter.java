package risesoft.data.transfer.core.factory.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

/**
 * 标识这是一个配置的字段
 * 
 * @typeName ConfigField
 * @date 2024年1月26日
 * @author lb
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ConfigParameter {

	/**
	 * 默认值
	 * 
	 * @return
	 */
	String value() default StringUtils.EMPTY;

	/**
	 * 获取的路径
	 * 
	 * @return
	 */
	String path() default StringUtils.EMPTY;

	/**
	 * 是否为必须
	 * 
	 * @return
	 */
	boolean required() default false;

	/**
	 * 描述
	 * 
	 * @return
	 */
	String description() default StringUtils.EMPTY;

	/**
	 * 可选值
	 * 
	 * @return
	 */
	String[] options() default {};

}
