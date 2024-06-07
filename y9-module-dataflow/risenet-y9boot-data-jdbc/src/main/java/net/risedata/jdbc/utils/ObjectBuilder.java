package net.risedata.jdbc.utils;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;

/**
 * 用于构建一个用于查询的对象
 * 
 * @author libo 2021年6月17日
 */
public class ObjectBuilder<T> {
	private BeanConfig beanConfig;
	private T t;

	/**
	 * @param beanConfig
	 */
	public ObjectBuilder(BeanConfig beanConfig,T t) {
		this.beanConfig = beanConfig;
		this.t = t;
	}

	/**
	 * 设置值
	 * 
	 * @param key   字段名
	 * @param value 值
	 * @return
	 */
	public ObjectBuilder<T> set(String key, Object value) {
		FieldConfig fc = beanConfig.getField(key);
		if (fc == null) {
			throw new NullPointerException(key + " is null");
		}
		try {
			fc.getField().set(this.t, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 获取构建好的对象
	 * 
	 * @return
	 */
	public T builder() {
		return this.t;
	}

	/**
	 * 获取 构建好的对象
	 * 
	 * @param key   需要构建的字段key
	 * @param value 对应的值
	 * @return
	 */
	public T builder(String key, Object value) {
		return set(key, value).builder();
	}
}
