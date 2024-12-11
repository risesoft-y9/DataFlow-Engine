package risesoft.data.transfer.stream.rdbms.out.columns;

/**
 * 空值默认为空字符的解析器
 * 
 * 
 * @typeName NullCharValuePreparedStatementHandle
 * @date 2024年12月11日
 * @author lb
 */
public abstract class NullCharValuePreparedStatementHandle implements PreparedStatementHandle{

	@Override
	public String nullValue() {
		return "''";
	}

}
