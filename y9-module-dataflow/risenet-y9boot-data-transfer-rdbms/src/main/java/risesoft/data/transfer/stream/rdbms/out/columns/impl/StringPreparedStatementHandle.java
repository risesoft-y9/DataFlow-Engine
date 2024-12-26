package risesoft.data.transfer.stream.rdbms.out.columns.impl;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.stream.rdbms.out.columns.NullCharValuePreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandleFactory;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * String 类型处理
 * 
 * @typeName StringPreparedStatementHandle
 * @date 2024年1月25日
 * @author lb
 */
public class StringPreparedStatementHandle extends NullCharValuePreparedStatementHandle implements PreparedStatementHandle,PreparedStatementHandleFactory {

	@Override
	public boolean isHandle(int type) {
		switch (type) {
		case Types.CHAR:
		case Types.NCHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.NVARCHAR:
		case Types.LONGNVARCHAR:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean isBigType() {
		return false;
	}

	@Override
	public void fillPreparedStatementColumnType(PreparedStatement preparedStatement, int columnIndex, Column column,
			DataBaseType dataBaseType, Triple<List<String>, List<Integer>, List<String>> resultSetMetaData)
			throws Exception {
		preparedStatement.setString(columnIndex, column.asString());
	}

	@Override
	public PreparedStatementHandle getPreparedStatementHandle(int type) {
		return this;
	}

}
