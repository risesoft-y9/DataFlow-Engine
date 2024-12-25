package net.risedata.jdbc.factory;

import java.util.concurrent.ConcurrentHashMap;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.exception.InstanceException;
import net.risedata.jdbc.utils.ObjectBuilder;

/**
 * 用于构建ObjectBuilder对象
 * 
 * @author libo 2021年6月17日
 */
public class ObjectBuilderFactory {

	/**
	 * 获取对应的objectBuilder对象
	 * 
	 * @param <T>
	 * @param objectClass
	 * @return
	 */
	public static <T> ObjectBuilder<T> builder(Class<T> objectClass) {
		BeanConfig bc = BeanConfigFactory.getInstance(objectClass);
		T value = null;
		try {
			value = objectClass.newInstance();
		} catch (Exception e) {
			throw new InstanceException("构建实例失败!异常信息:" + e.getMessage());
		}
		return new ObjectBuilder<T>(bc, value);
	}

	/**
	 * 创建一个objectBuilder对象并且填充值
	 * 
	 * @param <T>
	 * @param objectClass
	 * @param key
	 * @param value
	 * @return
	 */
	public static <T> ObjectBuilder<T> builder(Class<T> objectClass, String key, Object value) {
		ObjectBuilder<T> builder = builder(objectClass);
		return builder.set(key, value);
	}
}
