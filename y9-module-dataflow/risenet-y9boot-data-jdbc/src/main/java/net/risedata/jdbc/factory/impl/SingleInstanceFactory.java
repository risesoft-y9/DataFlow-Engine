package net.risedata.jdbc.factory.impl;

import java.util.HashMap;
import java.util.Map;

import net.risedata.jdbc.factory.InstanceFactory;

/**
 * 单例的工厂 在此工厂中创建的对象 都会缓存到 工厂中
 * 
 * @author libo 2021年2月9日
 */
public class SingleInstanceFactory implements InstanceFactory{
    private  Map<Class<?>, Object> cache =new HashMap<Class<?>, Object>();
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getInstance(Class<T> instanceType) throws InstantiationException ,IllegalAccessException{
		Object o = cache.get(instanceType);
		if (o==null) {
			try {
				o = instanceType.newInstance();
				cache.put(instanceType, o);
			} catch (IllegalAccessException e) {
				throw new InstantiationException("Failed to create object : "+e.getMessage());
			}
		}
		return (T) o;
	}
    
	public void putInstance(Class<?> instanceType,Object value) {
		cache.put(instanceType, value);
	}
}
