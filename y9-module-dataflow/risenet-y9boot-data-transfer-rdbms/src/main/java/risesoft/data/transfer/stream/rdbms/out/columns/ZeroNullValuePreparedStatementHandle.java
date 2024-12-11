package risesoft.data.transfer.stream.rdbms.out.columns;

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
	public String nullValue() {
		return "'0'";
	}

}
