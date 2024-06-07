package risesoft.data.transfer.stream.rdbms.out.columns.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * Date 类型处理
 * 
 * @typeName DatePreparedStatementHandle
 * @date 2024年1月25日
 * @author lb
 */
public class DatePreparedStatementHandle implements PreparedStatementHandle {

	@Override
	public boolean isHandle(int type) {

		return Types.DATE == type;

	}

	@Override
	public boolean isBigType() {
		return false;
	}

	@Override
	public void fillPreparedStatementColumnType(PreparedStatement preparedStatement, int columnIndex, Column column,
			DataBaseType dataBaseType, Triple<List<String>, List<Integer>, List<String>> resultSetMetaData)
			throws Exception {
		if (resultSetMetaData.getRight().get(columnIndex).equalsIgnoreCase("year")) {
			if (column.asBigInteger() == null) {
				preparedStatement.setString(columnIndex, null);
			} else {
				preparedStatement.setInt(columnIndex, column.asBigInteger().intValue());
			}
		} else {
			Date utilDate = null;
			java.sql.Date sqlDate = null;
			try {
				utilDate = column.asDate();
			} catch (Exception e) {
				throw new SQLException(String.format("Date 类型转换错误：[%s]", column));
			}
			if (null != utilDate) {
				sqlDate = new java.sql.Date(utilDate.getTime());
			}
			preparedStatement.setDate(columnIndex, sqlDate);
		}
	}

}
