package risesoft.data.transfer.core.record;

import java.util.List;

import risesoft.data.transfer.core.handle.Handle;

/**
 * 数据输入时会触发的handle 在输入通道中执行，存在多线程调用
 * 
 * @typeName RecordInHandle
 * @date 2023年12月8日
 * @author lb
 */
public interface RecordInHandle extends Handle {
	/**
	 * 从输入通道中进入一条数据
	 * 
	 * @param record
	 */
	void doIn(Record record);

	/**
	 * 从输入通道中进入一批数据
	 * 
	 * @param records
	 */
	void doIn(List<Record> records);
}
