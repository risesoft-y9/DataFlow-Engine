package risesoft.data.transfer.stream.rdbms.in.columns.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;

/**
 * string 类型处理
 * 
 * @typeName StringCreateColumnHandle
 * @date 2024年1月25日
 * @author lb
 */
public class StringCreateColumnHandle implements CreateColumnHandle {

	@Override
	public boolean isHandle(int type) {
		switch (type) {
		case Types.CHAR:
		case Types.NCHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.NVARCHAR:
		case Types.LONGNVARCHAR:
			return true;
		default:
			return false;
		}
	}

	private final byte[] EMPTY_CHAR_ARRAY = new byte[0];

	@Override
	public Column getColumn(ResultSet rs, ResultSetMetaData metaData, int index, String mandatoryEncoding)
			throws Exception {
		String rawData;
		if (StringUtils.isBlank(mandatoryEncoding)) {
			rawData = rs.getString(index);
		} else {
			rawData = new String((rs.getBytes(index) == null ? EMPTY_CHAR_ARRAY : rs.getBytes(index)),
					mandatoryEncoding);
		}
		return new StringColumn(rawData, metaData.getColumnLabel(index));
	}

}
