package risesoft.data.transfer.stream.rdbms.out.columns.impl;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * Blob 类型处理
 * 
 * @typeName BlobPreparedStatementHandle
 * @date 2024年1月25日
 * @author lb
 */
public class BlobPreparedStatementHandle implements PreparedStatementHandle {

	@Override
	public boolean isHandle(int type) {

		return Types.BLOB == type;

	}

	@Override
	public boolean isBigType() {
		return true;
	}

	@Override
	public void fillPreparedStatementColumnType(PreparedStatement preparedStatement, int columnIndex, Column column,
			DataBaseType dataBaseType, Triple<List<String>, List<Integer>, List<String>> resultSetMetaData)
			throws Exception {
		byte[] bytes = column.asBytes();
		if (bytes == null) {
			preparedStatement.setBytes(columnIndex, bytes);
		} else {
			preparedStatement.setBlob(columnIndex, new ByteArrayInputStream(column.asBytes()));
		}
	}

	@Override
	public String nullValue() {
		return "''";
	}

}
