package risesoft.data.transfer.core.action;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.plug.Plug;

/**
 * 动作插件 预留了前置和后置但未使用 
 * 
 * @typeName ActionPlug
 * @date 2023年12月7日
 * @author lb
 */
public class ActionPlug implements Plug {

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
		return false;
	}

}
