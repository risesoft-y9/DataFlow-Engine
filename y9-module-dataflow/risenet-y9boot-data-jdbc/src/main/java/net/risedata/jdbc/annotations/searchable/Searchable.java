package net.risedata.jdbc.annotations.searchable;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于一个bean的查询配置 通常是这个bean的查询条件过多的时候使用
 *  
 * @author libo 2020年11月30日
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Searchable {
	/**
	 * 指定的entiry对象 指定之后 sreachable 里面的字段名字需和指定的entiry对象对应
	 * 
	 * @return
	 */
	Class<?> value();
	/**
	 * 指定其他的表名
	 * @return
	 */
	String tableName() default "";
	/**
	 * 映射是否与value 类相同  也就是是否使用value 的列
	 * 默认为true 
	 * 如果为false 则只会使用当前searchable的列映射
	 * @return
	 */
	boolean rows() default true;
}
