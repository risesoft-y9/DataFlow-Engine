package risesoft.data.transfer.core.factory;

import java.lang.reflect.Constructor;
import java.util.Map;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.factory.annotations.ConfigField;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 默认产生实例的工厂
 * 
 * @typeName DefaultCreateInstanceFactory
 * @date 2023年12月6日
 * @author lb
 */
public class DefaultCreateInstanceFactory implements InstanceFactory {

	@Override
	public <T> T getInstance(String name, Class<T> cla, Map<Class<?>, Object> instanceMap) throws Exception {
		Class<?> class1 = Thread.currentThread().getContextClassLoader().loadClass(name);
		// TODO 可以在此处去修改 class对象
		@SuppressWarnings("rawtypes")
		Constructor constructor = class1.getConstructors()[0];
		Class<?>[] instanceClasses = constructor.getParameterTypes();
		if (instanceClasses.length == 0) {
			return cla.cast(constructor.newInstance());
		}
		Object[] instanceObjects = new Object[instanceClasses.length];
		Configuration configuration = (Configuration) instanceMap.get(Configuration.class);
		for (int i = 0; i < instanceClasses.length; i++) {
			ConfigParameter configField = constructor.getParameters()[i].getAnnotation(ConfigParameter.class);
			instanceObjects[i] =BeanFactory.getOjbect(instanceClasses[i], configField, configuration, instanceMap,
					constructor.getParameters()[i].getName());

		}
		return cla.cast(constructor.newInstance(instanceObjects));
	}



}
