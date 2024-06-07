package net.risedata.jdbc.operation;

import java.lang.reflect.Field;

/**
 * 初始化操作
 * 
 * @author libo 2021年3月29日
 */
public interface OperationInit {
	/**
	 * 初始化字段完成后会调用此方法 (注意:是初始化字段不是初始化这个类 如果这个bean是单例的则会调用多次)
	 * 
	 * @param beanClass
	 * @param field
	 */
	void initial(Class<?> beanClass, Field field);
}
