package risesoft.data.transfer.stream.rdbms.out.columns;

import java.sql.PreparedStatement;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * 执行批量输出到数据库时各类型的处理器
 * 
 * @typeName PreparedStatementHandle
 * @date 2023年12月21日
 * @author lb
 */
public interface PreparedStatementHandle {
	/**
	 * 是否为大类型
	 * 
	 * @return
	 */
	boolean isBigType();

	/**
	 * 设置值
	 * 
	 * @param preparedStatement statement
	 * @param columnIndex       当前列的index也就是需要设置的index
	 * @param column            列数据
	 * @param dataBaseType      数据库类型
	 * @param resultSetMetaData 存放字段的源信息
	 * @throws Exception
	 */
	void fillPreparedStatementColumnType(PreparedStatement preparedStatement, int columnIndex, Column column,
			DataBaseType dataBaseType, Triple<List<String>, List<Integer>, List<String>> resultSetMetaData)
			throws Exception;

	/**
	 * 这个主要用于update 时当目标字段为空时的空值比较,避免因为值为NULL导致无法更新问题
	 * 
	 * @return
	 */
	String nullValue();
}
