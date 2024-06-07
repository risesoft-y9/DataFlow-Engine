package risesoft.data.transfer.stream.rdbms.out;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * insert 操作的流
 * 
 * @typeName InsertRdbmsDataOutputStream
 * @date 2023年12月21日
 * @author lb
 */
public class InsertRdbmsDataOutputStream extends RdbmsDataOutputStream {

	public InsertRdbmsDataOutputStream(Connection connection, String workSql,
			Triple<List<String>, List<Integer>, List<String>> resultSetMetaData,
			Map<String, PreparedStatementHandle> createCloumnHandles, DataBaseType dataBaseType, Logger logger) {
		super(connection, workSql, resultSetMetaData, createCloumnHandles, dataBaseType, logger);
	}

	protected PreparedStatement fillPreparedStatement(PreparedStatement preparedStatement, Record record)
			throws Exception {
		int size = this.resultSetMetaData.getLeft().size();
		Map<String, Column> recordMap = getRecordMap(record);
		Column column;
		PreparedStatementHandle psHandle;
		for (int i = 0; i < size; i++) {
			psHandle = this.createCloumnHandles.get(this.resultSetMetaData.getLeft().get(i));
			column = recordMap.get(this.resultSetMetaData.getLeft().get(i));
			if (column == null) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR,
						"没找到字段通常是配置少了字段导致的:" + this.resultSetMetaData.getLeft().get(i));
			}
			psHandle.fillPreparedStatementColumnType(preparedStatement, i + 1, column, dataBaseType, resultSetMetaData);
		}
		return preparedStatement;
	}

}
