package risesoft.data.transfer.core.log;

import risesoft.data.transfer.core.handle.Handle;

/**
 * 标准日志输出
 * 
 * @typeName LogHandle
 * @date 2023年12月22日
 * @author lb
 */
public interface LogInfoHandle extends Handle{
	/**
	 * 
	 * @param source 日志源
	 * @param info   信息
	 * @param name   名字
	 */
	void info(Object source, String info);

}
