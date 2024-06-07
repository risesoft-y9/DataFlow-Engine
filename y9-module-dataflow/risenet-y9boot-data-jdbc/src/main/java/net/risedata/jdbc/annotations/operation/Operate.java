package net.risedata.jdbc.annotations.operation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.risedata.jdbc.factory.OperationFactory;
import net.risedata.jdbc.operation.Operates;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.operation.impl.NullOperation;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(OperateCollection.class)
public @interface Operate {
	/**
	 * 对应的操作
	 * @return
	 */
	Operates value() ;
	
	/**
	 * 什么条件下生效 ps: 一个字段只会有一个条件生效
	 */
	String expression() default "";
	/**
	 * 排序的级别越小越优先
	 * @return
	 */
	int level() default 1;
	/**
	 * 自定义的操作 使用自定义的class 必须 将value 设置为placehold
	 *     OperationFactory 会去加载这个 operation 
	 *      一般情况下使用 defaultGetInstanceFactory 去加载 此operation
	 *       defaultGetInstanceFactory 默认为单例工厂也可以在自定义的operation中添加@factory注解去指定工厂
	 *      具体方法:{@link OperationFactory}
	 */
	Class<? extends Operation> operation() default NullOperation.class;
}
