package risesoft.data.transfer.core.stream.out;

import java.util.List;

import risesoft.data.transfer.core.record.Ack;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.stream.DataStream;

/**
 * 数据输出流用于输出数据
 * 
 * @typeName DataOutputStream
 * @date 2023年12月4日
 * @author lb
 */
public interface DataOutputStream extends DataStream {

	/**
	 * 输出一批数据返回输出结果
	 * 
	 * @param records
	 * @return
	 */
	void writer(List<Record> records,Ack ack);

	/**
	 * 输出一条数据
	 * 
	 * @param record
	 */
	void writer(Record record,Ack ack);

}
