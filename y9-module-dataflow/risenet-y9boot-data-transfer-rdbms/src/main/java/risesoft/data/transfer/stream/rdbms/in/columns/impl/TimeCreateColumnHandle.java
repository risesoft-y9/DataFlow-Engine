package risesoft.data.transfer.stream.rdbms.in.columns.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.DateColumn;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;

/**
 * time 类型处理
 * 
 * @typeName TimeCreateColumnHandle
 * @date 2024年1月25日
 * @author lb
 */
public class TimeCreateColumnHandle implements CreateColumnHandle {

	@Override
	public boolean isHandle(int type) {
		return Types.TIME == type;

	}

	@Override
	public Column getColumn(ResultSet rs, ResultSetMetaData metaData, int index, String mandatoryEncoding)
			throws Exception {
		return new DateColumn(rs.getTime(index), metaData.getColumnLabel(index));
	}

}
