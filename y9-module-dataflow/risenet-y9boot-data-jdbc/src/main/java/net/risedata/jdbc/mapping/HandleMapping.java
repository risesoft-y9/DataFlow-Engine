package net.risedata.jdbc.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.risedata.jdbc.executor.set.SetValueExecutor;

/**
 * 字段映射类型处理器
 */
public interface HandleMapping<T> {
	/**
	 * 是否由此字段处理器处理
	 * @param cla
	 * @return
	 */
     boolean isHandle(Class<?> cla);
     /**
      * 处理
      * @param o 对象
      * @param field 字段
      * @param setValueExecutor 设置值的执行器
      * @param type 类型
      * @param value 值
      * @throws IllegalAccessException
      * @throws IllegalArgumentException
      * @throws InvocationTargetException
      */
     void handle(Object o,Field field,SetValueExecutor<Field> setValueExecutor,Class<?> type,T value)throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
     
     /**
      * get值
      * @param set
      * @param field
      * @return
      * @throws SQLException
      */
     T getValue(ResultSet set,String field)throws SQLException;
}
