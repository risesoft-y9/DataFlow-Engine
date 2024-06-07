package risesoft.data.transfer.core.record;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.handle.Handle;

/**
 * 为了避免多个字段处理时多次循环问题提供的一次循环调用的类
 * 
 * @typeName ColumnDispose
 * @date 2024年3月18日
 * @author lb
 */
@FunctionalInterface
public interface ColumnDisposeHandle extends Handle {

	Column dispose(Column column, Record record,int index);
}
