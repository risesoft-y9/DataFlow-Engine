package net.risedata.jdbc.factory.impl;

import net.risedata.jdbc.factory.InstanceFactory;

/**
 * 多例工厂 在此工厂中创建的对象 不会被缓存
 * 
 * @author libo 2021年2月9日
 */
public class MultitonInstanceFactory implements InstanceFactory {

	@Override
	public <T> T getInstance(Class<T> instanceType) throws InstantiationException, IllegalAccessException {
		return instanceType.newInstance();
	}



}
