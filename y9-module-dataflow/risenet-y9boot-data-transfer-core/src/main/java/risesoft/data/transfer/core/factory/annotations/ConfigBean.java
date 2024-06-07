package risesoft.data.transfer.core.factory.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识是一个配置的bean
 * 
 * @typeName ConfigBean
 * @date 2024年1月26日
 * @author lb
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ConfigBean {

	
}
