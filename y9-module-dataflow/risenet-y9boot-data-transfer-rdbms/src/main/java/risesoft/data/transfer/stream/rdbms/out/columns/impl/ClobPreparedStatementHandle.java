package risesoft.data.transfer.stream.rdbms.out.columns.impl;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * clob 类型处理
 * 
 * @typeName ClobPreparedStatementHandle
 * @date 2024年1月25日
 * @author lb
 */
public class ClobPreparedStatementHandle implements PreparedStatementHandle {

	@Override
	public boolean isHandle(int type) {
		switch (type) {
		case Types.CLOB:
		case Types.NCLOB:
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
		String data = column.asString();
		if (data == null) {
			preparedStatement.setClob(columnIndex, new StringReader(""));
		} else {
			preparedStatement.setClob(columnIndex, new StringReader(data));
		}
	}

}
