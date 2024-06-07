package risesoft.data.transfer.stream.rdbms.in.columns.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.BoolColumn;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;

/**
 * Boolean 类型处理
 * 
 * @typeName BooleanCreateColumnHandle
 * @date 2024年1月25日
 * @author lb
 */
public class BooleanCreateColumnHandle implements CreateColumnHandle {

	@Override
	public boolean isHandle(int type) {
		switch (type) {
	      case Types.BOOLEAN:
          case Types.BIT:
			return true;
		default:
			return false;
		}

	}

	@Override
	public Column getColumn(ResultSet rs, ResultSetMetaData metaData, int index, String mandatoryEncoding)
			throws Exception {

		return new BoolColumn(rs.getBoolean(index),metaData.getColumnLabel(index));

	}

}
