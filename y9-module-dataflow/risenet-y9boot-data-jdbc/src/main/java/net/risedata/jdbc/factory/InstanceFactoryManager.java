package net.risedata.jdbc.factory;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.core.annotation.AnnotationUtils;

import net.risedata.jdbc.annotations.factory.Factory;
import net.risedata.jdbc.exception.InstanceException;
import net.risedata.jdbc.factory.impl.MultitonInstanceFactory;
import net.risedata.jdbc.factory.impl.SingleInstanceFactory;

/**
 * 管理所有的instance工厂
 * 
 * @author libo 2021年2月9日
 */
public class InstanceFactoryManager {
	private static final Map<Class<? extends InstanceFactory>, InstanceFactory> INSTANCEFACTORY_MAP = new HashMap<Class<? extends InstanceFactory>, InstanceFactory>();
	static {
		init(new SingleInstanceFactory());
		init(new MultitonInstanceFactory());
	}

	/**
	 * 将一个工厂实例注册到工厂中
	 * 
	 * @param factory
	 */
	public static void init(@NotNull InstanceFactory factory) {
		assert (factory != null) : " factory is null";
		INSTANCEFACTORY_MAP.put(factory.getClass(), factory);
	}

	/**
	 * 拿到一个实例工厂
	 * 
	 * @param factoryClass
	 * @return
	 */
	public static InstanceFactory getInstanceFactory(Class<? extends InstanceFactory> factoryClass) {
		assert (factoryClass != null) : " factory is null";
		return INSTANCEFACTORY_MAP.get(factoryClass);
	}

	public static <T> T getInstance(Class<T> type) {
		Factory factory = AnnotationUtils.findAnnotation(type, Factory.class);
		try {
			if (factory != null) {
				InstanceFactory instanceFactory = getInstanceFactory(factory.value());
				if (instanceFactory == null) {
					throw new InstanceException("instance factory is null " + factory.value());
				}
				return instanceFactory.getInstance(type);
			}
			return getInstanceFactory(MultitonInstanceFactory.class).getInstance(type);
		} catch (Exception e) {
			throw new InstanceException(type + " create instance error " + e.getMessage());
		}
	}

}
