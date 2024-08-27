package risesoft.data.transfer.core.config;

import java.util.ArrayList;
import java.util.List;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 加载配置 ps: 预留给以后更多配置接入
 * 
 * @typeName ConfigLoad
 * @date 2024年8月27日
 * @author lb
 */
public class ConfigLoadManager {

	private static final List<ConfigLoad> CONFIG_LOADS = new ArrayList<ConfigLoad>();

	/**
	 * 解析配置
	 * 
	 * @param config
	 * @param jobContext
	 * @return
	 */
	public static Configuration loadConfig(Configuration config,JobContext jobContext) {
		for (ConfigLoad configLoad : CONFIG_LOADS) {
			config = configLoad.laod(config,jobContext);
		}
		return config;
	}

	/**
	 * 从指令中解析配置
	 * 
	 * @param config
	 * @return
	 */
	public static void addLoad(ConfigLoad configLoad) {
		CONFIG_LOADS.add(configLoad);
	}

}
