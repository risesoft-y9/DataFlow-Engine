package risesoft.data.transfer.stream.rdbms.out.columns.impl;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.out.columns.ZeroNullValuePreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * Number 类型处理
 * 
 * @typeName NumberPreparedStatementHandle
 * @date 2024年1月25日
 * @author lb
 */
public class NumberPreparedStatementHandle extends ZeroNullValuePreparedStatementHandle implements PreparedStatementHandle {

	@Override
	public boolean isHandle(int type) {
		switch (type) {
		case Types.SMALLINT:
		case Types.INTEGER:
		case Types.BIGINT:
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
	public boolean isBigType() {
		return false;
	}

	@Override
	public void fillPreparedStatementColumnType(PreparedStatement preparedStatement, int columnIndex, Column column,
			DataBaseType dataBaseType, Triple<List<String>, List<Integer>, List<String>> resultSetMetaData)
			throws Exception {
		Double strValue = column.asDouble();
		if (strValue == null) {
			preparedStatement.setString(columnIndex , null);
		} else {
			// 解决double精度问题
//			preparedStatement.setString(columnIndex + 1, strValue);
			if (column.asString().indexOf(".") != -1) {
				preparedStatement.setDouble(columnIndex , strValue);
			} else {
				preparedStatement.setBigDecimal(columnIndex , column.asBigDecimal());
			}

		}
	}

}
