package risesoft.data.transfer.stream.rdbms.out.columns;

import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

public abstract class DateNullValuePreparedStatementHandle implements PreparedStatementHandle {

	@Override
	public String nullValue(DataBaseType dataBaseType) {
		switch (dataBaseType) {
		case MySql:
			return "sysdate()";
		case PostgreSQL:
			return "now()";
		case SQLServer:
			return "GETDATE()";
		default:
			return "sysdate";
		}
	}

}
