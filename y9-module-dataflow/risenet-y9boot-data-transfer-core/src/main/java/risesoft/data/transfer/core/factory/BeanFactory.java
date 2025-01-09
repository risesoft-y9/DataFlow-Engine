package risesoft.data.transfer.core.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.ErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigBean;
import risesoft.data.transfer.core.factory.annotations.ConfigField;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.FieldUtils;
import risesoft.data.transfer.core.util.strings.ValueCastHandleFactory;

/**
 * 构建bean
 * 
 * @typeName BeanFactory
 * @date 2024年1月26日
 * @author lb
 */
public class BeanFactory {
	/**
	 * 缓存
	 */
	private static final Map<Class<?>, ConfigCache> CACHE = new HashMap<Class<?>, BeanFactory.ConfigCache>();

	/**
	 * 根据配置文件生产bean
	 * 
	 * @param <T>
	 * @param instanceClass
	 * @param configuration
	 * @return
	 */
	public static <T> T getInstance(Class<T> instanceClass, Configuration configuration,
			Map<Class<?>, Object> instanceMap) {
		ConfigCache configCache = getCache(instanceClass);
		if (configCache != null) {
			try {
				ConfigConstructor constructor = configCache.constructor;
				Object[] args = new Object[constructor.parameters.length];
				Parameter[] parameters = constructor.parameters;
				for (int i = 0; i < parameters.length; i++) {
					args[i] = getParameterValue(configuration, parameters[i].getAnnotation(ConfigParameter.class), parameters[i].getName(), instanceClass,
							instanceMap);
				}
				Object instanceObject = constructor.constructor.newInstance(args);
				Object value;
				String strValue;
				for (ConfigFieldCache fieldCache : configCache.fields) {

					value = configuration.get(fieldCache.configField.path().equals(StringUtils.EMPTY) ? fieldCache.name
							: fieldCache.configField.path(), fieldCache.type);
					if (value == null) {
						if (fieldCache.configField.value() != StringUtils.EMPTY) {
							value = ValueCastHandleFactory.castValue(fieldCache.configField.value(), fieldCache.type);
						} else if (fieldCache.configField.required()) {
							throw new RuntimeException("缺失必要的配置:" + fieldCache.configField.description());
						}
					}
					if (value != null) {
						if (fieldCache.configField.options().length > 0) {
							strValue = value.toString();
							if (!fieldCache.options.contains(strValue)) {
								throw new RuntimeException("非法参数:" + strValue + " "
										+ fieldCache.configField.description() + " 可选值为:" + fieldCache.options);
							}
						}
						fieldCache.setValue.set(instanceObject, value, instanceMap, configuration);
					}
				}
				return instanceClass.cast(instanceObject);
			} catch (Exception e) {
				throw TransferException.as(CommonErrorCode.CONFIG_ERROR, "配置装配失败:" + e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * 获取参数值
	 * 
	 * @param configuration
	 * @param configField
	 * @param name
	 * @param type
	 * @param insMap
	 * @return
	 */
	public static Object getParameterValue(Configuration configuration, ConfigParameter configField, String name,
			Class<?> type, Map<Class<?>, Object> insMap) {
		Object value;
		String strValue;
		value = configuration.get(configField.path().equals(StringUtils.EMPTY) ? name : configField.path(), type);
		if (value == null) {
			if (configField.value() != StringUtils.EMPTY) {
				value = ValueCastHandleFactory.castValue(configField.value(), type);
			} else if (configField.required()) {
				throw new RuntimeException("缺失必要的配置:" + configField.description());
			}
		}
		if (value != null) {
			if (configField.options().length > 0) {
				strValue = value.toString();
				boolean flag = false;
				for (String optionValue : configField.options()) {
					if (optionValue.equals(strValue)) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					throw new RuntimeException(
							"非法参数:" + strValue + " " + configField.description() + " 可选值为:" + configField.options());
				}
			}
		}
		return value;
	}

	/**
	 * 获取对象
	 * 
	 * @param type          需要获取的类型
	 * @param parameter     parameter对象
	 * @param configuration 配置信息
	 * @param instanceMap   实例map
	 * @param name          参数名字
	 * @return
	 */
	public static Object getOjbect(Class<?> type, ConfigParameter parameter, Configuration configuration,
			Map<Class<?>, Object> instanceMap, String name) {
		Object object = instanceMap.get(type);
		if (object == null) {
			if (parameter != null) {
				object = BeanFactory.getParameterValue(configuration, parameter, name, type, instanceMap);
			} else {
				object = BeanFactory.getInstance(type, configuration, instanceMap);
			}
		}
		return object;
	}

	private static ConfigCache getCache(Class<?> instanceClass) {
		ConfigCache configCache = CACHE.get(instanceClass);
		if (configCache == null && instanceClass.getAnnotation(ConfigBean.class) != null) {
			configCache = new ConfigCache();
			configCache.fields = new ArrayList<BeanFactory.ConfigFieldCache>();
			List<Field> fields = FieldUtils.getFields(instanceClass);
			ConfigField configField;
			String methodName;
			Method setMethod;
			Method[] methods = instanceClass.getMethods();
			if (instanceClass.getConstructors().length==0) {
				throw TransferException.as(CommonErrorCode.CONFIG_ERROR, instanceClass+" 不存在构造函数!");
			}
			Constructor<?> constructors = instanceClass.getConstructors()[0];
			constructors.setAccessible(true);
			Parameter[] parameters = constructors.getParameters();
			configCache.constructor = new ConfigConstructor(constructors, parameters);
			for (Field field : fields) {
				configField = field.getAnnotation(ConfigField.class);
				if (configField != null) {
					methodName = "set" + capitalizeFirstLetter(field.getName());
					setMethod = null;
					for (Method method : methods) {
						if (method.getName().equals(methodName)) {
							setMethod = method;
							break;
						}
					}
					if (setMethod != null) {
						setMethod.setAccessible(true);
						configCache.fields.add(new ConfigFieldCache(configField, field.getType(),
								new MethodSetValue(setMethod), field.getName()));
					} else {
						field.setAccessible(true);
						configCache.fields.add(new ConfigFieldCache(configField, field.getType(),
								new FiledSetValue(field), field.getName()));
					}
				}
			}
		}
		return configCache;
	}

	private static String capitalizeFirstLetter(String str) {

		char firstChar = Character.toUpperCase(str.charAt(0));

		String remainingStr = str.substring(1);

		return firstChar + remainingStr;
	}

	/**
	 * 缓存
	 * 
	 * @typeName ConfigCache
	 * @date 2024年1月26日
	 * @author lb
	 */
	static class ConfigCache {

		private List<ConfigFieldCache> fields;

		private ConfigConstructor constructor;

	}

	static class FiledSetValue implements SetValue {

		private Field field;

		public FiledSetValue(Field field) {
			this.field = field;
		}

		@Override
		public void set(Object source, Object valueObject, Map<Class<?>, Object> insMap, Configuration configuration)
				throws Exception {
			try {
				field.set(source, valueObject);
			} catch (Exception e) {
				throw e;
			}
		}

	}

	static class MethodSetValue implements SetValue {

		private Method method;

		private Parameter[] parameters;

		public MethodSetValue(Method method) {
			this.method = method;
			this.parameters = this.method.getParameters();
		}

		@Override
		public void set(Object source, Object valueObject, Map<Class<?>, Object> insMap, Configuration configuration)
				throws Exception {
			try {
				if (parameters.length > 1) {
					Object[] parameterValues = new Object[parameters.length];
					parameterValues[0] = valueObject;
					for (int i = 1; i < parameterValues.length; i++) {
						parameterValues[i] = getOjbect(parameters[i].getType(),
								parameters[i].getAnnotation(ConfigParameter.class), configuration, insMap,
								parameters[i].getName());
					}
					method.invoke(source, parameterValues);
					return;
				}
				method.invoke(source, valueObject);
			} catch (Exception e) {
				throw e;
			}
		}

	}

	static class ConfigConstructor {

		private Constructor<?> constructor;

		private Parameter[] parameters;

		public ConfigConstructor(Constructor<?> constructor, Parameter[] parameters) {
			super();
			this.constructor = constructor;
			this.parameters = parameters;
		}

	}

	/**
	 * 字段配置缓存
	 * 
	 * @typeName ConfigFieldCache
	 * @date 2024年1月26日
	 * @author lb
	 */
	static class ConfigFieldCache {

		private ConfigField configField;
		/**
		 * 字段源
		 */
		private SetValue setValue;

		private HashSet<String> options;

		private String name;

		private Class<?> type;

		public ConfigFieldCache(ConfigField configField, Class<?> type, SetValue setValue, String name) {
			super();
			this.configField = configField;
			this.type = type;
			this.setValue = setValue;
			this.name = name;
			if (configField.options().length > 0) {
				options = new HashSet<String>();
				options.addAll(Arrays.asList(configField.options()));
			}
		}

	}
}
