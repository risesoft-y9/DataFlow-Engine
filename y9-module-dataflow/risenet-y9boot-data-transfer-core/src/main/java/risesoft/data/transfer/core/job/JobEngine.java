package risesoft.data.transfer.core.job;

import risesoft.data.transfer.core.Engine;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.context.StreamContext;
import risesoft.data.transfer.core.executor.ExecutorListenerAdapter;
import risesoft.data.transfer.core.executor.in.JobInputExecutorFactory;
import risesoft.data.transfer.core.executor.out.JobOutputExecutorFactory;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.statistics.State;
import risesoft.data.transfer.core.util.CloseUtils;

/**
 * 任务引擎控制任务执行任务,由组装引擎组装好后交由控制器执行
 * 
 * @typeName JobEngine
 * @date 2023年12月8日
 * @author lb
 */
public class JobEngine {
	/**
	 * 启动任务
	 * 
	 * @param jobContext
	 */
	public static JobContext start(JobContext jobContext) {
		jobContext.getCommunication().reset();
		startJob(jobContext);
		return jobContext;
	}

	/**
	 * 启动任务
	 * 
	 * @param jobContext
	 */
	public static void startJob(JobContext jobContext) {
		Job job;
		StreamContext streamContext;
		try {

			Communication communication = jobContext.getCommunication();
			if (jobContext.hasJob()) {
				jobContext.doHandle(JobStartHandle.class, (h) -> {
					h.onJobStart(jobContext);
				});
				job = jobContext.nextJob();
				JobRunningController jobRunningController = new JobRunningController(jobContext);
				streamContext = job.getStreamContext();
				streamContext.getDataInputStreamFactory().init();
				streamContext.getDataOutputStreamFactory().init();
				// 切换输入输出工厂的消费者和生产者
				jobContext.getInExecutorTaskQueue().setExecutorFacoty(
						new JobInputExecutorFactory(jobContext, streamContext.getDataInputStreamFactory()));
				jobContext.getOutExecutorTaskQueue().setExecutorFacoty(
						new JobOutputExecutorFactory(streamContext.getDataOutputStreamFactory(), jobContext));
				// 没有一个切换对象工厂的事情
				jobContext.getInExecutorTaskQueue().setExecutorListener(new ExecutorListenerAdapter() {
					public void onError(Throwable e) {
						jobRunningController.onError(e);
					}

					@Override
					public void taskEnd(Object task) {
						// TODO 此处当有任务结束了的时候调用核心交换机flush数据确保不会数据丢失
						jobContext.getCoreExchange().flush();
						jobRunningController.inEnd();
						// 输入工厂的结束只会触发一次因为添加任务时是同步的此时需要保存结束时间
						communication.setLongCounter(CommunicationTool.READ_JOB_END, System.currentTimeMillis());
					}

					@Override
					public void taskStart(Object task) {
						// TODO 任务启动记录读取时间
						if (communication.getLongCounter(CommunicationTool.READ_JOB_START) == 0) {
							communication.setLongCounter(CommunicationTool.READ_JOB_START, System.currentTimeMillis());
						}

					}

				});
				jobContext.getOutExecutorTaskQueue().setExecutorListener(new ExecutorListenerAdapter() {

					public void onError(Throwable e) {
						jobRunningController.onError(e);
					}

					@Override
					public void taskEnd(Object task) {
						if (jobRunningController.isEnd()) {
							communication.setLongCounter(CommunicationTool.WRITER_JOB_END, System.currentTimeMillis());
							jobRunningController.outEnd();
						}
					}

					@Override
					public void taskStart(Object task) {
						// TODO 任务启动记录读取时间
						if (communication.getLongCounter(CommunicationTool.WRITER_JOB_START) == 0) {
							communication.setLongCounter(CommunicationTool.WRITER_JOB_START,
									System.currentTimeMillis());
						}
					}
				});
				jobContext.getInExecutorTaskQueue().addBatch(streamContext.getDataInputStreamFactory()
						.splitToData(jobContext.getInExecutorTaskQueue().getExecutorSize()));
				jobContext.getOutExecutorTaskQueue().start();
				jobContext.getInExecutorTaskQueue().start();
			} else {
				jobContext.getCommunication().setState(State.SUCCEEDED);
			}
		} catch (Throwable e) {
			jobContext.getCommunication().setThrowable(e, true);
			jobContext.getCommunication().setState(State.FAILED);
		} finally {
			Engine.onJobFlush(jobContext);
		}
	}

}
