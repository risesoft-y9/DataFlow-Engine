package risesoft.data.transfer.core.handle;

import risesoft.data.transfer.core.record.Record;

/**
 * 脏数据处理
 * 
 * @typeName DirtyRecordHandle
 * @date 2023年12月12日
 * @author lb
 */
public interface DirtyRecordHandle extends Handle {

	void collectDirtyRecord(Record record, Throwable t, String errorMessage);
}
