package risesoft.data.transfer.stream.rdbms.in.columns.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.BytesColumn;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;

/**
 * Bytes 类型处理
 * 
 * @typeName BytesCreateColumnHandle
 * @date 2024年1月25日
 * @author lb
 */
public class BytesCreateColumnHandle implements CreateColumnHandle {

	@Override
	public boolean isHandle(int type) {
		switch (type) {
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.BLOB:
		case Types.LONGVARBINARY:
			return true;
		default:
			return false;
		}

	}

	@Override
	public Column getColumn(ResultSet rs, ResultSetMetaData metaData, int index, String mandatoryEncoding)
			throws Exception {

		return new BytesColumn(rs.getBytes(index), metaData.getColumnLabel(index));

	}

}
