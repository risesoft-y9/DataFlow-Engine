package net.risedata.jdbc.executor.set.impl;

import java.lang.reflect.Field;

import net.risedata.jdbc.executor.set.SetValueExecutor;

/**
 * 用字段来设置一个对象的值的执行类
 * 
 * @author libo 2021年2月8日
 */
public class FieldSetValueExecutor implements SetValueExecutor<Field> {

	@Override
	public void setValue(Object o, Object value, Field field) {
		if (value == null) {
			return;
		}
		try {
		
			field.set(o, value);
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {
         
			e.printStackTrace();
		}

	}

}
