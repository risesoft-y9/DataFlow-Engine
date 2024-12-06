package risesoft.data.transfer.stream.rdbms.in.columns.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;


import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;

/**
 * 大文本类型处理
 * 
 * @typeName ClobCreateColumnHandle
 * @date 2024年1月25日
 * @author lb
 */
public class ClobCreateColumnHandle implements CreateColumnHandle {

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
	public Column getColumn(ResultSet rs, ResultSetMetaData metaData, int index, String mandatoryEncoding)
			throws Exception {
		return new StringColumn(rs.getString(index), metaData.getColumnLabel(index));
	}

}
