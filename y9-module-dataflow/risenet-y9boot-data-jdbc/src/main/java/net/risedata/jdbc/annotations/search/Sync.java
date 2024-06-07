package net.risedata.jdbc.annotations.search;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识这个bean 在查询分页的时候开启异步 
 * 使用这个功能之前需要先注入一个TaskAwaitExecutor 
 *  
 * @author libo 2020年11月30日
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Sync {
	
}
