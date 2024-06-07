package risesoft.data.transfer.base.exchange;

import java.util.ArrayList;
import java.util.List;


import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.StrUtil;

/**
 * 直流限速交换机
 * 
 * @typeName BufferRecordMoneyExchange
 * @date 2023年12月11日
 * @author lb
 */
public class DCMoneyExchange extends MoneyExchange {

	/**
	 * 缓冲交换机
	 * 
	 * @param configuration
	 */
	public DCMoneyExchange(Configuration configuration, LoggerFactory loggerFactory) {
		super(configuration, loggerFactory.getLogger(configuration.getString("name", "BufferRecordMoneyExchange")));

		logger.info(this, "DC record created \n speedByte: " +StrUtil.stringify(speedByte)  + " \n speedRecord: " + speedRecord
				+ " \n time: " + (speed/1000) + "/s");
	}

	@Override
	public synchronized void writer(List<Record> record) {
		if (isShutdown) {
			return;
		}
		if (logger.isDebug()) {
			logger.debug(this, "writer " + record.size());
		}
		this.await((int) CommunicationTool.getRecordSize(record), record.size());
		this.channel.writer(new ArrayList<Record>(record));
	}

	@Override
	public synchronized void flush() {
		if (isShutdown) {
			return;
		}
		if (logger.isDebug()) {
			logger.debug(this, "flush ");
		}
		this.channel.flush();

	}

	@Override
	protected void writerRecord(Record record) {
		this.channel.writer(record);
	}
}
