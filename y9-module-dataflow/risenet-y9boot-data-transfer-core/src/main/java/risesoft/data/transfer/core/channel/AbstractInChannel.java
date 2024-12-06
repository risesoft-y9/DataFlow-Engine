package risesoft.data.transfer.core.channel;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.handle.DirtyRecordHandle;
import risesoft.data.transfer.core.handle.HandleManager;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.stream.out.RecordOutuptStream;

/**
 * 抽象类
 *  所有输入通道的父类
 *  包含脏数据收集统计
 * @typeName AbstractInChannel
 * @date 2023年12月11日
 * @author lb
 */
public abstract class AbstractInChannel implements InChannel {

	protected RecordOutuptStream recordOutuptStream;
	protected Communication communication;
	private HandleManager handleManager;

	public AbstractInChannel(JobContext jobContext) {
		this.communication = jobContext.getCommunication();
		this.handleManager = jobContext.getHandles();
	}

	@Override
	public void setOutPutStream(RecordOutuptStream recordOutuptStream) {
		this.recordOutuptStream = recordOutuptStream;

	}

	@Override
	public void close() throws Exception {
		recordOutuptStream.close();

	}

	public void collectDirtyRecord(Record record, Throwable t, String errorMessage) {
		communication.increaseCounter(CommunicationTool.READ_FAILED_BYTES, record.getByteSize());
		communication.increaseCounter(CommunicationTool.READ_FAILED_RECORDS, 1);
		handleManager.doHandle(DirtyRecordHandle.class, (handle)->{
			handle.collectDirtyRecord(record, t, errorMessage);
		});
	}

}
