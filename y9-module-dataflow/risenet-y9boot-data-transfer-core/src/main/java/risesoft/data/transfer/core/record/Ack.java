package risesoft.data.transfer.core.record;

import java.util.List;

/**
 * 消息接收
 * 
 * @typeName Ack
 * @date 2023年12月12日
 * @author lb
 */
public interface Ack {
	/**
	 * 确定
	 * 
	 * @param record
	 */
	void confirm(Record record);

	/**
	 * 确定
	 * 
	 * @param record
	 */
	void confirm(List<Record> record);

	/**
	 * 取消
	 * 
	 * @param record
	 * @param e
	 * @param errorMag
	 */
	void cancel(Record record, Throwable e, String errorMag);
}
