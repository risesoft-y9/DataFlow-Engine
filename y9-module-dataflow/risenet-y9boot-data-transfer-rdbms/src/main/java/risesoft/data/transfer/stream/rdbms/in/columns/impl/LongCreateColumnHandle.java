package risesoft.data.transfer.stream.rdbms.in.columns.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;


import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.LongColumn;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;

/**
 * long 类型处理
 * 
 * @typeName LongCreateColumnHandle
 * @date 2024年1月25日
 * @author lb
 */
public class LongCreateColumnHandle implements CreateColumnHandle {

	@Override
	public boolean isHandle(int type) {
		switch (type) {
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.INTEGER:
		case Types.BIGINT:
			return true;
		default:
			return false;
		}

	}

	@Override
	public Column getColumn(ResultSet rs, ResultSetMetaData metaData, int index, String mandatoryEncoding)
			throws Exception {
		return new LongColumn(rs.getString(index), metaData.getColumnLabel(index));
	}

}
