package risesoft.data.transfer.core.channel;

import java.util.Arrays;
import java.util.List;

import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.executor.ExecutorTaskQueue;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.stream.out.RecordOutuptStream;
import risesoft.data.transfer.core.util.CloseUtils;

/**
 * 连接输出器的通道
 * 
 * @typeName JoinOutExecutorChannel
 * @date 2023年12月8日
 * @author lb
 */
public class JoinOutExecutorChannel implements OutChannel {

	private ExecutorTaskQueue outExecutor;

	public JoinOutExecutorChannel(ExecutorTaskQueue outExecutor) {
		super();
		this.outExecutor = outExecutor;
	}

	@Override
	public void setOutPutStream(RecordOutuptStream recordOutuptStream) {
		throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "底层通道无法修改连接器");

	}

	@Override
	public void writer(Record record) {
		this.outExecutor.add(Arrays.asList(record));
	}

	@Override
	public void writer(List<Record> record) {
		this.outExecutor.add(record);
	}

	@Override
	public void close() {
		CloseUtils.close(this.outExecutor);
	}

	@Override
	public void flush() {
		
	}

}
