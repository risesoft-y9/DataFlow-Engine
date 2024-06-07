package risesoft.data.transfer.core.stream.out;

import java.util.List;

import risesoft.data.transfer.core.close.Closed;
import risesoft.data.transfer.core.record.Record;

/**
 * 数据输出流
 * 
 * @typeName RecordOutuptStream
 * @date 2023年12月4日
 * @author lb
 */
public interface RecordOutuptStream extends Closed {
	/**
	 * 输出一条record
	 * 
	 * @param record
	 */
	void writer(Record record);

	/**
	 * 批量输出数据
	 * 
	 * @param record
	 */
	void writer(List<Record> record);
	
	void flush();

}
