package risesoft.data.transfer.stream.rdbms.out.columns;

import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * 空值默认为零的解析器
 * 
 * 
 * @typeName ZeroNullValuePreparedStatementHandle
 * @date 2024年12月11日
 * @author lb
 */
public abstract class ZeroNullValuePreparedStatementHandle implements PreparedStatementHandle{

	@Override
	public String nullValue(DataBaseType dataBaseType) {
		return "'0'";
	}

}
