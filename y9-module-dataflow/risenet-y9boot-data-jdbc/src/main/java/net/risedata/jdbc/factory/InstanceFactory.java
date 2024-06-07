package net.risedata.jdbc.factory;
/**
 * 实例工厂用于创建类
 * @author libo
 *2021年2月9日
 */
public interface InstanceFactory {
	/**
	 * 传入class对象拿到对应的实例
	 * @param <T>
	 * @param instanceType
	 * @return
	 */
    <T>T getInstance(Class<T> instanceType)throws InstantiationException,IllegalAccessException;
    
}
