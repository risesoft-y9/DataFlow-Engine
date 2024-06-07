package risesoft.data.transfer.core.executor.out;


import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.executor.Executor;
import risesoft.data.transfer.core.executor.ExecutorFacotry;
import risesoft.data.transfer.core.stream.out.DataOutputStreamFactory;
import risesoft.data.transfer.core.util.CloseUtils;

/**
 * 任务运行时输入连接的执行工厂 连接的是输入输出工厂与执行器
 * 
 * @typeName JobExecutorFactory
 * @date 2023年12月8日
 * @author lb
 */
public class JobOutputExecutorFactory implements ExecutorFacotry {

	private DataOutputStreamFactory dataOutputStreamFactory;
	private JobContext jobContext;

	public JobOutputExecutorFactory(DataOutputStreamFactory dataOutputStreamFactory, JobContext jobContext) {
		this.dataOutputStreamFactory = dataOutputStreamFactory;
		this.jobContext = jobContext;
	}

	@Override
	public Executor getInstance() {
		return new OutChannelExecutor(dataOutputStreamFactory.getStream(),jobContext.getCommunication(),jobContext.getHandles());
	}

	@Override
	public void close() {
		CloseUtils.close(dataOutputStreamFactory);

	}

}
