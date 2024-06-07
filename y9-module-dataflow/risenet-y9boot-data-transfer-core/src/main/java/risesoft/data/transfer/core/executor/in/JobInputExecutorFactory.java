package risesoft.data.transfer.core.executor.in;



import risesoft.data.transfer.core.channel.HandleInChannel;
import risesoft.data.transfer.core.channel.InChannel;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.executor.Executor;
import risesoft.data.transfer.core.executor.ExecutorFacotry;
import risesoft.data.transfer.core.record.RecordInHandle;
import risesoft.data.transfer.core.stream.in.DataInputStreamFactory;
import risesoft.data.transfer.core.util.CloseUtils;

/**
 * 任务运行时输入连接的执行工厂
 * 
 * @typeName JobExecutorFactory
 * @date 2023年12月8日
 * @author lb
 */
public class JobInputExecutorFactory implements ExecutorFacotry {

	private JobContext JobContext;

	private DataInputStreamFactory dataInputStreamFactory;

	public JobInputExecutorFactory(JobContext jobContext, DataInputStreamFactory dataInputStream) {
		super();
		JobContext = jobContext;
		this.dataInputStreamFactory = dataInputStream;
	}

	@Override
	public Executor getInstance() {
		InChannel inChannel = JobContext.getInChannel();
		if (JobContext.getHandles().hasHandle(RecordInHandle.class)) {
			inChannel = new HandleInChannel(inChannel, JobContext.getHandles());
		}
		return new InChannelExecutor(dataInputStreamFactory.getStream(), inChannel);
	}

	@Override
	public void close() {
		CloseUtils.close(this.dataInputStreamFactory);
		CloseUtils.close(JobContext.getCoreExchange());
	}

}
