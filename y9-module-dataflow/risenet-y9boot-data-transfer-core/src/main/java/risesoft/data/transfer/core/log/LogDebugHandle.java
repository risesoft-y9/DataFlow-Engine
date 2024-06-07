package risesoft.data.transfer.core.log;

import risesoft.data.transfer.core.handle.Handle;

/**
 * 标准日志输出debug
 * 
 * @typeName LogHandle
 * @date 2023年12月22日
 * @author lb
 */
public interface LogDebugHandle extends Handle{
	/**
	 * 
	 * @param source 日志源
	 * @param debug  内容
	 * @param name   名字
	 */
	void debug(Object source, String debug);

}
