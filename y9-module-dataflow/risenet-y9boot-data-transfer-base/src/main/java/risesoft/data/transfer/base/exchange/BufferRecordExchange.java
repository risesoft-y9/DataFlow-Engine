package risesoft.data.transfer.base.exchange;

import java.util.ArrayList;
import java.util.List;

import risesoft.data.transfer.core.exchange.OutChannelExchange;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 缓冲交换机 自带一个缓冲值需要缓冲成组并输出到输出器 通道将任务切分 无限流功能
 * 
 * @typeName BufferExchange
 * @date 2023年12月11日
 * @author lb
 */
public class BufferRecordExchange extends OutChannelExchange {
	/**
	 * 缓冲条数
	 */
	private int bufferRecord;
	private ArrayList<Record> records;

	private Logger logger;

	private volatile boolean isShutdown = false;
	/**
	 * 缓冲交换机
	 * 
	 * @param configuration
	 */
	public BufferRecordExchange(Configuration configuration, LoggerFactory loggerFactory) {
		this.bufferRecord = configuration.getInt("bufferRecord", 1024);
		this.records = new ArrayList<Record>(this.bufferRecord);
		this.logger = loggerFactory.getLogger(configuration.getString("name", "BufferRecordExchange"));
		logger.info(this, "init");
	}

	@Override
	public synchronized void writer(Record record) {
		if (isShutdown) {
			return;
		}
		if (logger.isDebug()) {
			logger.debug(this, "writer: " + record);
		}
		records.add(record);
		if (records.size() == bufferRecord) {
			this.flush();
		}
	}

	@Override
	public synchronized void writer(List<Record> record) {
		if (isShutdown) {
			return;
		}
		if (logger.isDebug()) {
			logger.debug(this, "writer " + record.size());
		}
		int end = 0;
		int start = 0;
		while (end < record.size()) {
			end += bufferRecord - records.size();
			if (end > record.size()) {
				end = record.size();
			}
			this.records.addAll(new ArrayList<Record>(record.subList(start, end)));
			start = end;
			this.flush();
		}
	}

	@Override
	public synchronized void flush() {
		if (isShutdown) {
			return;
		}
		if (logger.isDebug()) {
			logger.debug(this, "flush " + records.size());
		}
		this.channel.writer(new ArrayList<Record>(this.records));
		this.channel.flush();
		this.records.clear();

	}



	@Override
	public void shutdown() {
		isShutdown = true;
		logger.info(this, "shutdown");
	}
	
	

}
