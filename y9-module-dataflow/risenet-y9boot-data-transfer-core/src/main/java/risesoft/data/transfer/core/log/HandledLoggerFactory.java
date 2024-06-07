package risesoft.data.transfer.core.log;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.handle.EmptyHandleContext;
import risesoft.data.transfer.core.handle.HandleContext;
import risesoft.data.transfer.core.handle.HandleManager;
import risesoft.data.transfer.core.handle.InitApplicationConfigHandle;
import risesoft.data.transfer.core.plug.Plug;
import risesoft.data.transfer.core.util.Configuration;

public class HandledLoggerFactory implements Plug,LoggerFactory, InitApplicationConfigHandle, LogHandleReference {
	private boolean debug;

	private boolean info;

	private boolean error;

	private HandleContext<LogDebugHandle> debugHandleContext;
	private HandleContext<LogInfoHandle> infoHandleContext;
	private HandleContext<LogErrorHandle> errorHandleContext;
	private HandleManager handleManager;

	public HandledLoggerFactory(HandleManager handleManager) {
		this.handleManager = handleManager;
	}

	@Override
	public Logger getLogger(String name) {
		return new HandledLogger(this, name);
	}

	@Override
	public Logger getLogger(Class<?> type) {
		return new HandledLogger(this, type.getName());
	}

	@Override
	public void initApplicationConfig(Configuration configuration) {
		this.debugHandleContext = handleManager.getContext(LogDebugHandle.class);
		this.infoHandleContext = handleManager.getContext(LogInfoHandle.class);
		this.errorHandleContext = handleManager.getContext(LogErrorHandle.class);
		this.debug = this.debugHandleContext != EmptyHandleContext.EMPTY;
		this.info = this.infoHandleContext != EmptyHandleContext.EMPTY;
		this.error = this.errorHandleContext != EmptyHandleContext.EMPTY;

	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	@Override
	public boolean isInfo() {
		return info;
	}

	@Override
	public boolean isError() {
		return error;
	}

	@Override
	public HandleContext<LogDebugHandle> getLogDebugHandle() {

		return debugHandleContext;
	}

	@Override
	public HandleContext<LogInfoHandle> getLogInfoHandle() {
		return infoHandleContext;
	}

	@Override
	public HandleContext<LogErrorHandle> getLogErrorHandle() {
		return errorHandleContext;
	}

	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

}
