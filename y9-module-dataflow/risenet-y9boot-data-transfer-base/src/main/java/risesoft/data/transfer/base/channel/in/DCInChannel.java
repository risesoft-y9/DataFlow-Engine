package risesoft.data.transfer.base.channel.in;

import java.util.List;

import risesoft.data.transfer.core.channel.AbstractInChannel;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 直流输入通道
 * 
 * @typeName DCInChannel
 * @date 2023年12月11日
 * @author lb
 */
public class DCInChannel extends AbstractInChannel {

	/**
	 * 直流交换机
	 * 
	 * @param configuration
	 */
	public DCInChannel(Configuration configuration, JobContext jobContext) {
		super(jobContext);
	}

	@Override
	public void writer(Record record) {
		recordOutuptStream.writer(record);
	}

	@Override
	public void writer(List<Record> record) {
		recordOutuptStream.writer(record);
	}

	@Override
	public void flush() {
		recordOutuptStream.flush();

	}

}
