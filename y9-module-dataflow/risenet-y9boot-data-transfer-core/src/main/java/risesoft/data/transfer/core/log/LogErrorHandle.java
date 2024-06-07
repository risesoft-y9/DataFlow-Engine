package risesoft.data.transfer.core.log;

import risesoft.data.transfer.core.handle.Handle;

/**
 * 标准日志输出异常日志
 * 
 * @typeName LogHandle
 * @date 2023年12月22日
 * @author lb
 */
public interface LogErrorHandle extends Handle{
	/**
	 * 
	 * @param source 日志源
	 * @param msg 内容
	 * @param name 发送日志的名字
	 */
	void error(Object source, String msg);
}
