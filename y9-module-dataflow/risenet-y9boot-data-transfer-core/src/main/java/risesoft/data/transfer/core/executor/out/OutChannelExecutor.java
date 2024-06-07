package risesoft.data.transfer.core.executor.out;

import java.util.List;

import risesoft.data.transfer.core.executor.Executor;
import risesoft.data.transfer.core.handle.DirtyRecordHandle;
import risesoft.data.transfer.core.handle.HandleManager;
import risesoft.data.transfer.core.record.Ack;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.stream.out.DataOutputStream;
import risesoft.data.transfer.core.util.CloseUtils;

/**
 * 输出通道执行器是输出通道的下游执行器用于执行data输出的 唯一链接对象执行器
 * 
 * @typeName OutChannelExecutor
 * @date 2023年12月8日
 * @author lb
 */
public class OutChannelExecutor implements Executor, Ack {

	private DataOutputStream dataOutputStream;
	private Communication communication;
	private HandleManager handleManager;

	public OutChannelExecutor(DataOutputStream dataOutputStream, Communication communication,
			HandleManager handleManager) {
		this.dataOutputStream = dataOutputStream;
		this.communication = communication;
		this.handleManager = handleManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(Object records) {
		this.dataOutputStream.writer((List<Record>) records, this);
	}

	public void close() {
		CloseUtils.close(this.dataOutputStream);
	}

	@Override
	public void confirm(Record record) {
		communication.increaseCounter(CommunicationTool.WRITE_RECEIVED_BYTES, record.getByteSize());
		communication.increaseCounter(CommunicationTool.WRITE_RECEIVED_RECORDS, 1);
	}

	@Override
	public void confirm(List<Record> record) {
		communication.increaseCounter(CommunicationTool.WRITE_RECEIVED_RECORDS, record.size());
		communication.increaseCounter(CommunicationTool.WRITE_RECEIVED_BYTES, CommunicationTool.getRecordSize(record));

	}

	@Override
	public void cancel(Record record, Throwable e, String errorMag) {
		// 目前是cancel 的是会处理接受任务标识任务处理好之后会交由脏数据收集器去处理脏数据
		confirm(record);
		communication.increaseCounter(CommunicationTool.WRITE_FAILED_RECORDS, 1);
		communication.increaseCounter(CommunicationTool.WRITE_FAILED_BYTES, record.getByteSize());
		handleManager.doHandle(DirtyRecordHandle.class, (handle) -> {
			handle.collectDirtyRecord(record, e, errorMag);
		});
	}
}
