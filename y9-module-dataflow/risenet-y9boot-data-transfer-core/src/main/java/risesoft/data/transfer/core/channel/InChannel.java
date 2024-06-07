package risesoft.data.transfer.core.channel;

import risesoft.data.transfer.core.record.Record;

/**
 * 通道 下链接输出器上链接交换机, 输入通道标识这是一个输入通道
 * 
 * @typeName Channel
 * @date 2023年12月4日
 * @author lb
 */
public interface InChannel extends Channel {
	/**
	 * 收集脏数据脏数据推往这
	 * 
	 * @param record
	 * @param t
	 * @param errorMessage
	 */
	void collectDirtyRecord(Record record, Throwable t, String errorMessage);
}
