package net.risedata.jdbc.mapping.impl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.Entity;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.mapping.RowMapping;

/**
 * 此entiry 将不存放在 handle工厂中用来做 entiry的映射
 * 
 * @author libo 2020年10月20日
 */
public class EntiryHandle extends SimpleHandleMapping<Object> {

	RowMapping<Object> rowMapping;

	/**
	 * 
	 * @param fields 连接的fields
	 * @param bc     为当前bean的config对象
	 */
	public EntiryHandle(List<FieldConfig> fields, BeanConfig bc) {

		rowMapping = new RowMapping<>(bc, fields);
	}

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla.getAnnotation(Entity.class) == null;
	}

	public static boolean isEntiryHandle(Class<?> cla) {
		return cla.getAnnotation(Entity.class) != null;
	}

	@Override
	Object getValue(Field field, Object value, Class<?> type) {
//		Object returnValue = null;
//		try { 暂未搞懂 原本写 returnValue 的用意?
//			returnValue = type.newInstance();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		try {
			return rowMapping.mapRow((ResultSet) value, 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getValue(ResultSet set, String field) throws SQLException {
		/**
		 * 在 resultset 中 拿到 所有 colum 的 map返回出去
		 */
		return set;
	}

}
