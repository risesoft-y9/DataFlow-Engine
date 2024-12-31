package risesoft.data.transfer.core.factory;

import java.util.Map;

import risesoft.data.transfer.core.util.Configuration;

/**
 * 设置值，用于bean的创建使用
 * 
 * 
 * @typeName SetValue
 * @date 2024年12月31日
 * @author lb
 */
public interface SetValue {

	void set(Object source, Object valueObject, Map<Class<?>, Object> instanceMap, Configuration configuration) throws Exception;

}
