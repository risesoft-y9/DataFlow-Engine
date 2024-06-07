package risesoft.data.transfer.core.factory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.exception.CommonErrorCode;
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
	public static <T> T getInstance(Class<T> instanceClass, Configuration configuration) {
		ConfigCache configCache = getCache(instanceClass);
		if (configCache != null) {
			try {
				Object instanceObject = instanceClass.newInstance();
				Object value;
				String strValue;
				for (ConfigFieldCache fieldCache : configCache.fields) {

					value = configuration
							.get(fieldCache.configField.path().equals(StringUtils.EMPTY) ? fieldCache.field.getName()
									: fieldCache.configField.path(), fieldCache.field.getType());
					if (value == null) {
						if (fieldCache.configField.value() != StringUtils.EMPTY) {
							value = ValueCastHandleFactory.castValue(fieldCache.configField.value(),
									fieldCache.field.getType());
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
						fieldCache.field.set(instanceObject, value);
					}
				}
				return instanceClass.cast(instanceObject);
			} catch (Exception e) {
				throw TransferException.as(CommonErrorCode.CONFIG_ERROR, "配置装配失败:" + e.getMessage(), e);
			}
		}
		return null;
	}

	public static Object getParameterValue(Configuration configuration, ConfigParameter configField, String name,
			Class<?> type) {
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

	private static ConfigCache getCache(Class<?> instanceClass) {
		ConfigCache configCache = CACHE.get(instanceClass);
		if (configCache == null && instanceClass.getAnnotation(ConfigBean.class) != null) {
			configCache = new ConfigCache();
			configCache.fields = new ArrayList<BeanFactory.ConfigFieldCache>();
			List<Field> fields = FieldUtils.getFields(instanceClass);
			ConfigField configField;
			for (Field field : fields) {
				configField = field.getAnnotation(ConfigField.class);
				if (configField != null) {
					field.setAccessible(true);
					configCache.fields.add(new ConfigFieldCache(configField, field));
				}
			}
		}
		return configCache;
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
		private Field field;

		private HashSet<String> options;

		public ConfigFieldCache(ConfigField configField, Field field) {
			super();
			this.configField = configField;
			this.field = field;
			if (configField.options().length > 0) {
				options = new HashSet<String>();
				options.addAll(Arrays.asList(configField.options()));
			}
		}

	}
}
