package risesoft.data.transfer.core.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.ErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ValueUtils;

/**
 * 工厂管理器管理工厂对象
 * 
 * @typeName FactoryManager
 * @date 2023年12月6日
 * @author lb
 */
public class FactoryManager {

	public static final String NAME = "name";
	public static final String FACTORY = "factory";

	private static InstanceFactory DEFAULT;

	private static Map<String, String> NAME_MAPPING = new HashMap<String, String>();

	private static ErrorCode NOINSTANCE = new ErrorCode() {

		@Override
		public String getDescription() {
			return "未找到工厂检查配置";
		}

		@Override
		public String getCode() {
			return "no factory";
		}
	};
	static {
		FactoryManager.setDefault(new DefaultCreateInstanceFactory());
	}
	private static final Map<String, InstanceFactory> INS_MAP = new HashMap<String, InstanceFactory>();

	public static void setDefault(InstanceFactory instanceFactory) {
		DEFAULT = instanceFactory;
	}

	public static void initFactory(String name, InstanceFactory instanceFactory) {
		INS_MAP.put(name, instanceFactory);
	}

	/**
	 * 添加一个别名映射
	 * 
	 * @param key
	 * @param value
	 */
	public static void putNameMapping(String key, String value) {
		NAME_MAPPING.put(key, value);
	}

	public static String getName(String name) {
		String nameMapping = NAME_MAPPING.get(name);
		return nameMapping == null ? name : nameMapping;
	}

	/**
	 * 获取工厂当名字为空的时候返回默认的工厂
	 * 
	 * @param name
	 * @return
	 */
	public static InstanceFactory getFactory(String name) {
		if (StringUtils.isEmpty(name)) {
			return DEFAULT;
		}
		InstanceFactory instanceFactory = INS_MAP.get(getName(name));
		if (instanceFactory == null) {
			TransferException.as(NOINSTANCE, "未找到名字为:" + name + "的工厂实例");
		}
		return instanceFactory;

	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getInstancesOfConfiguration(Configuration configuration, String key, Class<T> retClass,
			Map<Class<?>, Object> instanceMap) {
		List<Configuration> configurations = configuration.getListConfiguration(key);
		if (configurations != null) {
			List<T> retS = new ArrayList<T>();
			// 加载插件
			Configuration tmpConfiguration;
			Object tmp = instanceMap.get(Configuration.class);
			for (int i = 0; i < configurations.size(); i++) {
				tmpConfiguration = configurations.get(i);
				instanceMap.put(Configuration.class, tmpConfiguration);
				retS.add(FactoryManager.getInstanceOfConfiguration(tmpConfiguration, retClass, instanceMap));
			}
			instanceMap.put(Configuration.class, tmp);
			return retS;
		}
		return Collections.EMPTY_LIST;

	}

	public static <T> T getInstanceOfConfiguration(Configuration configuration, Class<T> retClass,
			Map<Class<?>, Object> instanceMap) {
		String name = getName(ValueUtils.getRequired(configuration.getString(NAME), "插件名称不能为空!"));

		String factory = getName(configuration.getString(FACTORY));
		try {
			Object tmp = instanceMap.get(Configuration.class);
			instanceMap.put(Configuration.class, configuration.getConfigurationNotNull("args"));
			T resT = getFactory(factory).getInstance(name, retClass, instanceMap);
			instanceMap.put(retClass.getClass(), resT);
			instanceMap.put(Configuration.class, tmp);
			return resT;
		} catch (TransferException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			
			throw TransferException.as(new ErrorCode() {
				@Override
				public String getDescription() {
					return "未选择合适的类无法创建";
				}

				@Override
				public String getCode() {
					return null;
				}
			}, "在创建实例时报错:" +e.getCause().getMessage() + ",实例名:" + name);
		}

	}
}
