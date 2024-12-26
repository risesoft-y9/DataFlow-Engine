package risesoft.data.transfer.stream.rdbms.out.columns.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandleFactory;
import risesoft.data.transfer.stream.rdbms.out.columns.ZeroNullValuePreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * TimeStamp 类型处理
 * 
 * @typeName TimeStampPreparedStatementHandle
 * @date 2024年1月25日
 * @author lb
 */
public class TimeStampPreparedStatementHandle extends ZeroNullValuePreparedStatementHandle implements PreparedStatementHandle,PreparedStatementHandleFactory {

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
	public boolean isBigType() {
		return false;
	}

	@Override
	public void fillPreparedStatementColumnType(PreparedStatement preparedStatement, int columnIndex, Column column,
			DataBaseType dataBaseType, Triple<List<String>, List<Integer>, List<String>> resultSetMetaData)
			throws Exception {
		Date utilDate = null;
		java.sql.Timestamp sqlTimestamp = null;
		try {
			utilDate = column.asDate();
		} catch (Exception e) {
			throw new SQLException(String.format("TIMESTAMP 类型转换错误：[%s]", column));
		}

		if (null != utilDate) {
			sqlTimestamp = new java.sql.Timestamp(utilDate.getTime());
		}
		preparedStatement.setTimestamp(columnIndex , sqlTimestamp);
	}

	@Override
	public PreparedStatementHandle getPreparedStatementHandle(int type) {
		return this;
	}

}
