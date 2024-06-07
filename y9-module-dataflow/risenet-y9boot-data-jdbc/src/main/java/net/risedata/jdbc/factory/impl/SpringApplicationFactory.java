package net.risedata.jdbc.factory.impl;

import org.springframework.context.ApplicationContext;

import net.risedata.jdbc.factory.InstanceFactory;
/**
 * 用spring application 作为工厂
 * @author libo
 *2021年2月9日
 */
public class SpringApplicationFactory implements InstanceFactory {
	private ApplicationContext application;

	public SpringApplicationFactory(ApplicationContext application) {
		this.application = application;
	}

	@Override
	public <T> T getInstance(Class<T> instanceType) throws InstantiationException, IllegalAccessException {
		return application.getBean(instanceType);
	}

}
