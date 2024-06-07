package net.risedata.jdbc.factory;

import java.util.concurrent.ConcurrentHashMap;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.utils.ObjectBuilder;

/**
 * 用于构建ObjectBuilder对象
 * 
 * @author libo 2021年6月17日
 */
public class ObjectBuilderFactory {

	private static ConcurrentHashMap<Class<?>, ThreadLocal<Object>> BEAN_MAP = new ConcurrentHashMap<>();

	private static Object THREAD_LOCK = new Object();

	/**
	 * 获取对应的objectBuilder对象
	 * 
	 * @param <T>
	 * @param objectClass
	 * @return
	 */
	public static <T> ObjectBuilder<T> builder(Class<T> objectClass) {
		BeanConfig bc = BeanConfigFactory.getInstance(objectClass);
		T value = getInstance(objectClass);
		return new ObjectBuilder<T>(bc, value);
	}

	public static <T> ObjectBuilder<T> newBuilder(Class<T> objectClass) {
		BeanConfig bc = BeanConfigFactory.getInstance(objectClass);

		T value = null;
		try {
			value = (T) objectClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ObjectBuilder<T>(bc, value);
	}

	/***
	 * 在基于thread local 的单例池中拿到对象 注意是单例的 但是会把单例对象的值设置为空的
	 * 
	 * @param <T>
	 * @param bc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> returnClass) {
		ThreadLocal<Object> threadLocal = getThreadLocal(returnClass);
		Object value = threadLocal.get();
		if (value == null) {
			synchronized (threadLocal) {
				value = threadLocal.get();
				if (value == null) {
					try {
						value = returnClass.newInstance();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					threadLocal.set(value);
				}
			}
		}
		return (T) value;
	}

	public static ThreadLocal<Object> getThreadLocal(Class<?> classType) {
		ThreadLocal<Object> threadLocal = BEAN_MAP.get(classType);
		if (threadLocal == null) {
			synchronized (THREAD_LOCK) {
				threadLocal = BEAN_MAP.get(classType);
				if (threadLocal == null) {
					threadLocal = new ThreadLocal<>();
					BEAN_MAP.put(classType, threadLocal);
				}
			}
		}
		return threadLocal;
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
