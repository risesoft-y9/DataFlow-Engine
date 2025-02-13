package risesoft.data.transfer.base.exchange;

import java.util.ArrayList;
import java.util.List;

import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.statistics.RecordSize;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.StrUtil;

/**
 * 缓冲限流交换机
 * 
 * @typeName BufferRecordMoneyExchange
 * @date 2023年12月11日
 * @author lb
 */
public class BufferRecordMoneyExchange extends MoneyExchange {
	/**
	 * 缓冲条数
	 */
	private int bufferRecord;
	private ArrayList<Record> records;

	/**
	 * 缓冲交换机
	 * 
	 * @param configuration
	 */
	public BufferRecordMoneyExchange(Configuration configuration, LoggerFactory loggerFactory) {
		super(configuration, loggerFactory.getLogger(configuration.getString("name", "BufferRecordMoneyExchange")));
		this.bufferRecord = configuration.getInt("bufferRecord", 1024);
		this.records = new ArrayList<Record>();
		logger.info(this,
				"buffer record created buffer:" + bufferRecord + "\n speedByte: " + StrUtil.stringify(speedByte)
						+ " \n speedRecord: " + speedRecord + " \n time: " + (speed / 1000) + "/s");
	}

	private void ofFlush() {
		if (records.size() == bufferRecord) {
			flushBuffer();
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
		RecordSize recordSize;
		while (end < record.size()) {
			end += bufferRecord - records.size();
			if (end > record.size()) {
				end = record.size();
			}
			recordSize = CommunicationTool.getRecordSizeOfSpeed(record, start, end, speedByte);
			end = recordSize.getRecord();
			this.await((int) recordSize.getSize(), end - start);
			this.records.addAll(new ArrayList<Record>(record.subList(start, end)));
			start = end;
			this.ofFlush();
		}
	}

	@Override
	public synchronized void flush() {
		if (isShutdown) {
			return;
		}
		flushBuffer();

	}

	private void flushBuffer() {
		if (logger.isDebug()) {
			logger.debug(this, "flush " + this.records.size());
		}
		this.channel.writer(new ArrayList<Record>(this.records));
		this.channel.flush();
		this.records.clear();
	}

	@Override
	protected void writerRecord(Record record) {
		this.await((int) record.getByteSize(), 1);
		records.add(record);
		this.ofFlush();
	}
}
