package risesoft.data.transfer.base.exchange;

import java.util.List;

import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.exchange.OutChannelExchange;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 限流交换机 不做数据缓存只做数据限流
 * 
 * @typeName MoneyExchange
 * @date 2023年12月11日
 * @author lb
 */
public abstract class MoneyExchange extends OutChannelExchange {

	/**
	 * 当前输出大小
	 */
	protected int byteSize;

	/**
	 * 当前输出条数
	 */
	protected int recordSize;
	/**
	 * 输出统计时间以秒为单位
	 */
	protected long time;
	/**
	 * 限速byte
	 */
	protected int speedByte;
	/**
	 * 限速条数
	 */
	protected int speedRecord;
	/**
	 * 一条数据输出所需的毫秒
	 */
	protected double recordTime;
	/**
	 * 每毫秒所可以流出的数据
	 */
	protected double byteTime;
	/**
	 * 速度单位
	 */
	protected int speed;

	protected Logger logger;

	/**
	 * 缓冲交换机
	 * 
	 * @param configuration
	 */
	public MoneyExchange(Configuration configuration, Logger logger) {
		this.byteSize = 0;
		this.recordSize = 0;
		this.speedByte = configuration.getInt("speed.byte", 0) * 1024;
		this.speedRecord = configuration.getInt("speed.record", 0);
		this.speed = configuration.getInt("speed.time", 1) * 1000;
		if (speedByte == 0 && speedRecord == 0) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "限速配置条数与大小不能同时为0,如不需要限速请使用其他交换机!");
		}
		this.recordTime = speedRecord <= 0 ? 0 : (double) speed / speedRecord;
		this.byteTime = speedByte <= 0 ? 0 : (double) speed / speedByte;
		this.logger = logger;
		this.time = System.currentTimeMillis();
	}

	@Override
	public synchronized void writer(Record record) {
		if (isShutdown) {
			return;
		}
		if (logger.isDebug()) {
			logger.debug(this, "writer " + record);
		}
		await(record.getByteSize(), 1);
		writerRecord(record);
	}

	/**
	 * 输出一条数据 money层已经限流
	 * 
	 * @param record
	 */
	protected abstract void writerRecord(Record record);

	protected synchronized void await(int size, int record) {
		if ((speedByte > 0 && speedByte < size)) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "配置的限速无法限制请调整限速大小!：限制大小:"+speedByte+"，实际数据大小："+size);
		}
		if ((speedRecord > 0 && speedRecord < record)) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "配置的限速无法限制请调整限速大小!：限制条数:"+speedRecord+"，实际数据条数："+record);
		}
		long concurrentTime = System.currentTimeMillis();
		int differTime = (int) (speed - (concurrentTime - time));
		if (differTime < 0) {
			time = concurrentTime;
			differTime = speed;
			recordSize = 0;
			byteSize = 0;
		}
		int sleepTime = (int) Math.max((recordSize + record) * recordTime, (byteSize + size) * byteTime) - speed;
		if (sleepTime > 0) {
			try {
				if (logger.isDebug()) {
					logger.debug(this, "sleep:" + differTime);
				}
				Thread.sleep(differTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			time = System.currentTimeMillis() - sleepTime;
			recordSize = (int) (sleepTime / recordTime);
			byteSize = (int) (sleepTime / byteTime);
		} else {
			byteSize += size;
			recordSize += record;
		}
	}

	@Override
	public abstract void writer(List<Record> record);

	@Override
	public abstract void flush();

	@Override
	public void shutdown() {
		isShutdown = true;
		logger.info(this, "shutdown");
	}

}
