package net.risedata.jdbc.mapping.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import net.risedata.jdbc.commons.utils.DateUtils;
import net.risedata.jdbc.executor.set.SetValueExecutor;
import net.risedata.jdbc.mapping.CastHandleMapping;
import net.risedata.jdbc.mapping.HandleMapping;

/**
 * 
 * 时间类型处理器
 * 
 * @author libo 2020年10月15日
 */
public class DateHandle implements HandleMapping<Date>, CastHandleMapping<Date> {

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla == Date.class || cla == java.sql.Date.class || cla == Timestamp.class;
	}

	@Override
	public Date getValue(ResultSet set, String field) throws SQLException {
		//拿到时间而不是date 因为时间才能装换成对应的类
		return set.getTimestamp(field);
	}

	@Override
	public Date toValue(Object o) {
		if (o instanceof Date) {
			return (Date) o;
		}
		if (o instanceof Timestamp) {
			return new Date(((Timestamp) o).getTime());
		}
		return DateUtils.parse(o.toString());
	}

	@Override
	public void handle(Object o, Field field, SetValueExecutor<Field> setValueExecutor, Class<?> type, Date value)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {   
		if (value!=null) {
			if (type==Timestamp.class) {
			
				field.set(o, value);
			}else if (type == java.sql.Date.class) {
				field.set(o,new java.sql.Date( value.getTime()));
			}else {
				field.set(o,(Date) value);
			}
		}

	}

}
