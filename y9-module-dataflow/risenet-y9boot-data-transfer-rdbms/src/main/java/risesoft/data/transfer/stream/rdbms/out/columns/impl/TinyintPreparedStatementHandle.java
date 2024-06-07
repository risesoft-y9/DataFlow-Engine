package risesoft.data.transfer.stream.rdbms.out.columns.impl;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * Number 类型处理
 * 
 * @typeName NumberPreparedStatementHandle
 * @date 2024年1月25日
 * @author lb
 */
public class TinyintPreparedStatementHandle implements PreparedStatementHandle {

	@Override
	public boolean isHandle(int type) {

		return Types.TINYINT == type;

	}

	@Override
	public boolean isBigType() {
		return false;
	}

	@Override
	public void fillPreparedStatementColumnType(PreparedStatement preparedStatement, int columnIndex, Column column,
			DataBaseType dataBaseType, Triple<List<String>, List<Integer>, List<String>> resultSetMetaData)
			throws Exception {
		Long longValue = column.asLong();
		if (null == longValue) {
			preparedStatement.setString(columnIndex, null);
		} else {
			preparedStatement.setString(columnIndex, longValue.toString());
		}
	}

}
