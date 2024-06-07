package risesoft.data.transfer.stream.rdbms.in.columns.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.DateColumn;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;

/**
 * TimeStamp 类型处理
 * 
 * @typeName TimeStampCreateColumnHandle
 * @date 2024年1月25日
 * @author lb
 */
public class TimeStampCreateColumnHandle implements CreateColumnHandle {

	@Override
	public boolean isHandle(int type) {
		switch (type) {
		case Types.TIMESTAMP:
		case -101:
			return true;
		default:
			return false;
		}

	}

	@Override
	public Column getColumn(ResultSet rs, ResultSetMetaData metaData, int index, String mandatoryEncoding)
			throws Exception {

		return new DateColumn(rs.getTimestamp(index), metaData.getColumnLabel(index));

	}

}
