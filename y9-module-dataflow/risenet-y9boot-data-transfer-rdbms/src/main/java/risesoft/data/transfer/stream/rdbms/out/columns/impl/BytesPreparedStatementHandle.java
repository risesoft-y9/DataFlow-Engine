package risesoft.data.transfer.stream.rdbms.out.columns.impl;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandleFactory;
import risesoft.data.transfer.stream.rdbms.out.columns.ZeroNullValuePreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * Bytes 类型处理
 * 
 * @typeName BytesPreparedStatementHandle
 * @date 2024年1月25日
 * @author lb
 */
public class BytesPreparedStatementHandle extends ZeroNullValuePreparedStatementHandle  implements PreparedStatementHandle,PreparedStatementHandleFactory {

	@Override
	public boolean isHandle(int type) {
		switch (type) {
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean isBigType() {
		return true;
	}

	@Override
	public void fillPreparedStatementColumnType(PreparedStatement preparedStatement, int columnIndex, Column column,
			DataBaseType dataBaseType, Triple<List<String>, List<Integer>, List<String>> resultSetMetaData)
			throws Exception {
		preparedStatement.setBytes(columnIndex, column.asBytes());
	}

	@Override
	public PreparedStatementHandle getPreparedStatementHandle(int type) {
		return this;
	}

}
