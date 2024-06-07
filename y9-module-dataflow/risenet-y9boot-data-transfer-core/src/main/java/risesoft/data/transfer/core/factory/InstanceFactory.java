package risesoft.data.transfer.core.factory;

import java.util.Map;


/**
 * 类工厂实际获取实例
 * @typeName FactoryManager
 * @date 2023年12月4日
 * @author lb
 */
public interface InstanceFactory {
	/**
	 * 获取实例
	 * @param <T>
	 * @param name 实例名
	 * @param cla  需要转换的类
	 * @return
	 */
    <T> T getInstance(String name,Class<T> cla,Map<Class<?>, Object> instanceMap) throws Exception;

}
