package net.risedata.jdbc.factory;

import java.lang.reflect.Field;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import net.risedata.jdbc.annotations.factory.Factory;
import net.risedata.jdbc.annotations.operation.Operate;
import net.risedata.jdbc.commons.TypeCheck;
import net.risedata.jdbc.factory.impl.SingleInstanceFactory;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.operation.OperationPack;
import net.risedata.jdbc.search.Operations;
import net.risedata.jdbc.search.exception.OperationException;

/**
 * 操作工厂
 * 
 * @author libo 2021年2月8日
 */
public class OperationFactory {

	private static InstanceFactory defaultInstanceFactory;

	public static Operation getDefaultOperation(Field field) {
		Operate o = field.getAnnotation(Operate.class);// 存在注解就使用默认的
		if (o != null) {
			Object ao = handleOperation(o);
			if (ao instanceof OperationPack) {
				throw new OperationException("The only operation requires a condition");
			}
			return (Operation) ao;
		}
		Id id = field.getAnnotation(Id.class);
		if (id != null) {
			return Operations.EQ;
		}
		if (TypeCheck.isNumber(field.getType())) {// 数字
			if (TypeCheck.isIntegerClass(field.getType())) {
				return Operations.EQ;
			}
			if (TypeCheck.isDoubleClass(field.getType())) {
				return Operations.GT;
			}
		} else if (TypeCheck.isArrayType(field.getType())) {// 数组 in
			return Operations.IN;
		} else if (field.getType() == String.class) {// String like
			return Operations.LIKE;
		}
		return Operations.EQ;
	}
    /**
     * 处理 operation注解 返回operation 或者OperationPack
     * @param operate
     * @return
     */
	public static Object handleOperation(Operate operate) {
		Object op = operate.value().value;
		if (op == Operations.PLACEHOLDER) {
			if (operate.operation() != Operations.PLACEHOLDER.getClass()) {
				op = OperationFactory.getOperation(operate.operation());
			} else {
				throw new OperationException(" Use placeholders but do not define their own operations ");
			}
		}
		if (!StringUtils.isBlank(operate.expression())) {// 有条件
			op = new OperationPack(operate.expression(), (Operation) op);
		}
		return op;
	}

	/**
	 * 拿到存在于工厂中的操作类 注意:当不存在的时候工厂会new 出这个operation之后加入静态工厂中 如果 增加了 禁止单例 则每次都会new
	 * 
	 * @param operationClass
	 * @param name
	 * @return
	 */
	public static Operation getOperation(@NotNull Class<? extends Operation> operationClass) {
		assert (operationClass != null) : "operationClass is null";
		if (defaultInstanceFactory == null) {
			defaultInstanceFactory = InstanceFactoryManager.getInstanceFactory(SingleInstanceFactory.class);
		}
		Factory factory = AnnotationUtils.findAnnotation(operationClass, Factory.class);
		try {
			if (factory == null) {
				return defaultInstanceFactory.getInstance(operationClass);
			}
			return InstanceFactoryManager.getInstanceFactory(factory.value()).getInstance(operationClass);
		} catch (Exception e) {
			if (e instanceof NullPointerException) {
				throw new OperationException("The specified factory is not registered factory manager" );
			}
			throw new OperationException("create operation appear exception " + e.getMessage());
		}
	}

	/**
	 * @return the defaultInstanceFactory
	 */
	public static InstanceFactory getDefaultInstanceFactory() {
		return defaultInstanceFactory;
	}

	/**
	 * 设置 没加注解的operation类的默认注解
	 */
	public static void setDefaultInstanceFactory(InstanceFactory defaultInstanceFactory) {
		OperationFactory.defaultInstanceFactory = defaultInstanceFactory;
	}

}
