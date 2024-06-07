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
 * Time 类型处理
 * 
 * @typeName TimePreparedStatementHandle
 * @date 2024年1月25日
 * @author lb
 */
public class TimePreparedStatementHandle implements PreparedStatementHandle {

	@Override
	public boolean isHandle(int type) {

		return Types.TIME == type;

	}

	@Override
	public boolean isBigType() {
		return false;
	}

	@Override
	public void fillPreparedStatementColumnType(PreparedStatement preparedStatement, int columnIndex, Column column,
			DataBaseType dataBaseType, Triple<List<String>, List<Integer>, List<String>> resultSetMetaData)
			throws Exception {
		Date utilDate = null;
		java.sql.Time sqlTime = null;
		try {
			utilDate = column.asDate();
		} catch (Exception e) {
			throw new SQLException(String.format("TIME 类型转换错误：[%s]", column));
		}
		if (null != utilDate) {
			sqlTime = new java.sql.Time(utilDate.getTime());
		}
		preparedStatement.setTime(columnIndex, sqlTime);
	}

}
