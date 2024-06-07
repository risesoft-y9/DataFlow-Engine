package risesoft.data.transfer.base.channel.in;

import java.util.ArrayList;
import java.util.List;

import risesoft.data.transfer.core.channel.AbstractInChannel;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 缓冲的输入通道
 * 
 * @typeName BufferInChannel
 * @date 2023年12月11日
 * @author lb
 */
public class BufferRecordInChannel extends AbstractInChannel {
	/**
	 * 缓冲条数
	 */
	private int bufferRecord;
	/**
	 * 存储缓冲数据使用的
	 */
	private List<Record> records;

	/**
	 * 缓冲交换机
	 * 
	 * @param configuration
	 */
	public BufferRecordInChannel(Configuration configuration, JobContext jobContext) {
		super(jobContext);
		this.bufferRecord = configuration.getInt("bufferRecord", 1024);
		this.records = new ArrayList<Record>(this.bufferRecord);
	}

	@Override
	public void writer(Record record) {
		this.records.add(record);
		if (this.records.size() == this.bufferRecord) {
			this.flush();
		}
	}

	@Override
	public void writer(List<Record> record) {
		for (Record record2 : record) {
			writer(record2);
		}
	}

	@Override
	public void flush() {
		if (!records.isEmpty()) {
			recordOutuptStream.writer(records);
			records.clear();
		}
	}

}
