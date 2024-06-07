package risesoft.data.transfer.core.plug;

import java.util.List;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.factory.FactoryManager;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 插件管理器
 * 
 * @typeName PlugManager
 * @date 2023年12月6日
 * @author lb
 */
public class PlugManager {

	public static final String PLUG_KEY = "core.plugs";

	/**
	 * 加载插件
	 * 
	 * @param configuration 配置文件
	 * @param jobContext    作业上下文
	 */
	public static void loadPlug(Configuration configuration, JobContext jobContext) {
		List<Plug> plugs = FactoryManager.getInstancesOfConfiguration(configuration, PLUG_KEY, Plug.class,
				jobContext.getInstanceMap());
		for (Plug plug : plugs) {
			if (plug.register(jobContext)) {
				jobContext.getLogger().info(jobContext, "register plug:" + plug.getClass());
				jobContext.getHandles().add(plug);
			}
		}
	}

}
