package net.risedata.jdbc.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.risedata.jdbc.config.Load;
import net.risedata.jdbc.config.model.BeanConfig;

/**
 * 管理所有的beanConfig
 * 
 * @author libo 2021年2月10日
 */
public final class BeanConfigFactory {
	private static final Map<Object, BeanConfig> configs = new ConcurrentHashMap<Object, BeanConfig>();
	private static final Object lock = new Object();

	public static void putBeanConfig(Class<?> entiry, BeanConfig beanConfig) {
		configs.put(entiry, beanConfig);
	}

	/**
	 * 将 beanconfig 放入工厂
	 * 
	 * @param id
	 * @param beanConfig
	 */
	public static void putBeanConfig(String id, BeanConfig beanConfig) {
		configs.put(id, beanConfig);
	}

	public static String getTableName(Class<?> type) {
		BeanConfig bc = getInstance(type);
		if (bc != null) {
			return bc.getTableName();
		}
		return null;
	}

	/**
	 * 判断一个id是否存在工厂
	 * 
	 * @param id
	 * @return
	 */
	public static boolean has(Object id) {
		return configs.containsKey(id);
	}

	/**
	 * 从工厂中获得一个实例的 bean config 如果不存在且无法加载 则会抛出异常 此异常是通过 断言判断的 如为空 请使用 -ea 进行调试
	 * 
	 * @param entiry
	 * @return
	 */
	public static BeanConfig getInstance(Object entiry) {
		Object id = entiry;
		Class<?> entiryClass = entiry.getClass();
		if (entiryClass != Class.class && entiryClass != String.class) {
			id = entiry.getClass();
		}
		BeanConfig config = configs.get(id);
		if (config == null && entiryClass != String.class) {
			synchronized (lock) {
				if (!configs.containsKey(id)) {
					Load.loadBean((Class<?>) id);
					config = configs.get(id);
				} else {
					config = configs.get(id);
				}

			}
		}
		assert config != null : entiry + " Cannot load configuration";
		return config;
	}

}
