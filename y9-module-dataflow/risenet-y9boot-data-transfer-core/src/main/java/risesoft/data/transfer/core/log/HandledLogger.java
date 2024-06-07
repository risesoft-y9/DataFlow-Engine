package risesoft.data.transfer.core.log;

import risesoft.data.transfer.core.handle.HandleContext;

/**
 * 根据是否有对应的handle 创建的日志管理器 这也是框架默认的
 * 
 * @typeName HandledLogger
 * @date 2023年12月25日
 * @author lb
 */
public class HandledLogger implements Logger {

	private String name;

	private LogHandleReference logHandleReference;

	public HandledLogger(LogHandleReference logHandleReference, String name) {
		super();
		this.name=name;
		this.logHandleReference = logHandleReference;
	}

	@Override
	public boolean isDebug() {
		return logHandleReference.isDebug();
	}

	@Override
	public boolean isInfo() {
		return logHandleReference.isInfo();
	}

	@Override
	public boolean isError() {
		return logHandleReference.isError();
	}

	@Override
	public void debug(Object source, String msg) {
		if (isDebug()) {
			String msgString = "DEBUG: " + name + ": " + msg;
			this.logHandleReference.getLogDebugHandle().handle((h) -> {
				h.debug(source, msgString);
			});
		}

	}

	@Override
	public void info(Object source, String msg) {
		if (isInfo()) {
			String msgString = "INFO: " + name + ": " + msg;
			this.logHandleReference.getLogInfoHandle().handle((h) -> {
				h.info(source, msgString);
			});
		}

	}

	@Override
	public void error(Object source, String msg) {
		if (isError()) {
			String msgString = "ERROR: " + name + ": " + msg;
			this.logHandleReference.getLogErrorHandle().handle((h) -> {
				h.error(source, msgString);
			});
		}
	}

}
