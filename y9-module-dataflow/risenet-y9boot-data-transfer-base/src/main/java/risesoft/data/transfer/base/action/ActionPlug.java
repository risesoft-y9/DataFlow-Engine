package risesoft.data.transfer.base.action;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.context.JobContextInit;
import risesoft.data.transfer.core.handle.InitApplicationConfigHandle;
import risesoft.data.transfer.core.plug.Plug;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 动作插件
 * 
 * @typeName ActionPlug
 * @date 2023年12月7日
 * @author lb
 */
public class ActionPlug implements Plug,InitApplicationConfigHandle {

	/**
	 * 获取构造函数
	 * 
	 * @param jobContext
	 */
	public ActionPlug(JobContext jobContext) {
	
		System.out.println("创建action" + jobContext);
	}

	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

	@Override
	public void initApplicationConfig(Configuration configuration) {
	System.out.println("可以操作配置文件");
		
	}

}
