package risesoft.data.transfer.core.job;

import risesoft.data.transfer.core.Engine;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.statistics.State;
import risesoft.data.transfer.core.util.CloseUtils;
import risesoft.data.transfer.core.util.StrUtil;

/**
 * 任务执行器执行时候的控制对象
 * 
 * @typeName JobRunningController
 * @date 2023年12月11日
 * @author lb
 */
public class JobRunningController {

	private boolean flag;

	private JobContext jobContext;

	private Communication communication;

	public JobRunningController(JobContext jobContext) {
		this.jobContext = jobContext;
		this.communication = jobContext.getCommunication();
		flag = false;
	}

	public synchronized void inEnd() {
		flag = true;
		end();
	}

	private synchronized void end() {
		long readBytes = communication.getLongCounter(CommunicationTool.READ_SUCCEED_BYTES);
		long writerReceived = communication.getLongCounter(CommunicationTool.WRITE_RECEIVED_BYTES);
		if (readBytes == writerReceived) {
			CloseUtils.close(jobContext.getInExecutorTaskQueue());
			CloseUtils.close(jobContext.getOutExecutorTaskQueue());
			JobEngine.startJob(jobContext);
		}

	}

	public synchronized void onError(Throwable e) {
		communication.setThrowable(e);
		communication.setState(State.FAILED, true);
		Engine.onJobFlush(jobContext);
	}

	public synchronized boolean outEnd() {
		if (flag) {
			end();
		}
		return flag;
	}
	
	public synchronized boolean isEnd() {
		return flag;
	}
}
