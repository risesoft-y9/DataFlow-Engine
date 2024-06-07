package net.risedata.jdbc.type.sql.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.risedata.jdbc.config.Load;
import net.risedata.jdbc.type.sql.SqlType;
/**
 * 提供一些基础的类型给解析器用
 * @author libo
 *2021年2月8日
 * @param <T>
 */
public abstract class BaseSqlType<T> implements SqlType {

	private Class<T> T;

	public BaseSqlType() {
		try {
			this.T = getT();
			Load.loadBean(T);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Class<?> getType(String type) {
		return T;
	}
/**
 * 拿到泛型T
 * @return
 * @throws InstantiationException
 * @throws IllegalAccessException
 */
	private Class<T> getT() throws InstantiationException, IllegalAccessException {
		Type sType = getClass().getGenericSuperclass();
		Type[] generics = ((ParameterizedType) sType).getActualTypeArguments();
		@SuppressWarnings("unchecked")
		Class<T> mTClass = (Class<T>) generics[0];
		return mTClass;
	}

}
