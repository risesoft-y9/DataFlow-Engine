package risesoft.data.transfer.stream.rdbms.in.columns.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.DoubleColumn;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;

/**
 * Double 类型处理
 * 
 * @typeName DoubleCreateColumnHandle
 * @date 2024年1月25日
 * @author lb
 */
public class DoubleCreateColumnHandle implements CreateColumnHandle {

	@Override
	public boolean isHandle(int type) {
		switch (type) {
		case Types.NUMERIC:
		case Types.DECIMAL:
		case Types.FLOAT:
		case Types.REAL:
		case Types.DOUBLE:
			return true;
		default:
			return false;
		}

	}

	@Override
	public Column getColumn(ResultSet rs, ResultSetMetaData metaData, int index, String mandatoryEncoding)
			throws Exception {
		return new DoubleColumn(rs.getString(index), metaData.getColumnLabel(index));
	}

}
