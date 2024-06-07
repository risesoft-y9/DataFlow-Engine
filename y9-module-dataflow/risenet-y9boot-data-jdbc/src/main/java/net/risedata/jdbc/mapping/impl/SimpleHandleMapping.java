package net.risedata.jdbc.mapping.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.risedata.jdbc.executor.set.SetValueExecutor;
import net.risedata.jdbc.mapping.HandleMapping;

/**
 * 处理值的基础类提供了基础的包装方法
 * @author libo
 *2021年2月8日
 */
public abstract class SimpleHandleMapping<T> implements HandleMapping<T>{


	@Override
	public void handle(Object o,Field field,SetValueExecutor<Field> setValueExecutor,Class<?> type,T value)
    		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	setValueExecutor.setValue(o, getValue(field, value,type), field);
    };
    
	/**
	 * 根据实现类本身的get出来的value再根据实际的字段返回对应的value
	 *    实现类可以根据情况来决定是否实现改方法
	 * @param field 字段
	 * @param value 从实现类中getvalueget出来的值
	 * @param type 类型
	 * @return
	 */
	 Object getValue(Field field,T value,Class<?> type) {
		 return value;
	 };
	
	 
	 T toValue(ResultSet set , T value) {
		 try {
			if (set.wasNull()) {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 return value;
	 }



}
