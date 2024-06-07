package risesoft.data.transfer.core.log;

import risesoft.data.transfer.core.handle.HandleContext;

/**
 * 日志连接
 * 
 * @typeName LogReference
 * @date 2023年12月29日
 * @author lb
 */
public interface LogHandleReference {

	boolean isDebug();

	boolean isInfo();

	boolean isError();
   
	HandleContext<LogDebugHandle> getLogDebugHandle();

	HandleContext<LogInfoHandle> getLogInfoHandle();

	HandleContext<LogErrorHandle> getLogErrorHandle();
}
