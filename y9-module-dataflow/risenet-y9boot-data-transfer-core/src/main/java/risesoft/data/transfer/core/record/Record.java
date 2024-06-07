package risesoft.data.transfer.core.record;

/**
 * 一条数据
 * @typeName Record
 * @date 2023年12月4日
 * @author lb
 */

import java.util.List;

import risesoft.data.transfer.core.column.Column;

public interface Record {
	/**
	 * 获取数据大小
	 * 
	 * @return
	 */
	int getByteSize();

	/**
	 * 获取数据
	 * 
	 * @return
	 */
	List<Column> getColumns();

	/**
	 * 添加一列
	 * 
	 * @param column
	 */
	void addColumn(Column column);

	/**
	 * 设置指定列
	 * 
	 * @param i
	 * @param column
	 */
	void setColumn(int i, final Column column);

	/**
	 * 获取指定的列
	 * 
	 * @param i
	 * @return
	 */
	Column getColumn(int i);

	/**
	 * toString
	 * 
	 * @return
	 */
	String toString();

	/**
	 * 获取有多少列
	 * 
	 * @return
	 */
	int getColumnNumber();

	/**
	 * 获取内存消耗
	 * 
	 * @return
	 */
	long getMemorySize();
}
