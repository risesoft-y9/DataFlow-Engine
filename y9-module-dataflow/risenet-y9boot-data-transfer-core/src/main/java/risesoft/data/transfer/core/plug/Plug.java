package risesoft.data.transfer.core.plug;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.handle.Handle;

/**
 * 各个模块的插件，此插件可以操作到执行中的操作，这个插件只是一个标记，标记这个类属于插件类型，具体需要实现handle来决定
 * 
 * @typeName Plug
 * @date 2023年12月4日
 * @author lb
 */
public interface Plug extends Handle {
	/**
	 * 是否注册
	 * 
	 * @param jobContext
	 * @return
	 */
	boolean register(JobContext jobContext);
}
