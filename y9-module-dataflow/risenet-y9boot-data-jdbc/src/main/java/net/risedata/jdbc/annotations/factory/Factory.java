package net.risedata.jdbc.annotations.factory;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.risedata.jdbc.factory.InstanceFactory;

/**
 * 增加的此注解的bean 会在bean工厂中选择对应的工厂进行初始化
 * @author libo
 *2021年2月9日
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Factory {

    Class<? extends InstanceFactory> value();
}
