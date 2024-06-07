package risesoft.data.transfer.stream.rdbms.in.columns.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;

/**
 * Null 类型处理
 * 
 * @typeName NullCreateColumnHandle
 * @date 2024年1月25日
 * @author lb
 */
public class NullCreateColumnHandle implements CreateColumnHandle {

	@Override
	public boolean isHandle(int type) {
		return Types.NULL == type;

	}

	@Override
	public Column getColumn(ResultSet rs, ResultSetMetaData metaData, int index, String mandatoryEncoding)
			throws Exception {
		String stringData = null;
		if (rs.getObject(index) != null) {
			stringData = rs.getObject(index).toString();
		}
		return new StringColumn(stringData, metaData.getColumnLabel(index));

	}

}
