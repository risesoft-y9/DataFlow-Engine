package risesoft.data.transfer.stream.rdbms.out;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.util.ValueUtils;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * insert 操作的流
 * 
 * @typeName InsertRdbmsDataOutputStream
 * @date 2023年12月21日
 * @author lb
 */
public class UpdateRdbmsDataOutputStream extends RdbmsDataOutputStream {
	/**
	 * id 字段
	 */
	private List<String> idField;
	/**
	 * 修改的字段
	 */
	private List<String> updateField;

	public UpdateRdbmsDataOutputStream(Connection connection, String workSql,
			Triple<List<String>, List<Integer>, List<String>> resultSetMetaData,
			Map<String, PreparedStatementHandle> createColumnHandles, DataBaseType dataBaseType, List<String> idField,
			List<String> updateField,Logger logger) {
		super(connection, workSql, resultSetMetaData, createColumnHandles, dataBaseType,logger);
		this.idField = idField;
		this.updateField = updateField;
	}

	protected PreparedStatement fillPreparedStatement(PreparedStatement preparedStatement, Record record)
			throws Exception {
		int size = 1;
		Map<String, Column> colMap = getRecordMap(record);
		Column col;
		PreparedStatementHandle psHandle;
		// id
		for (int i = 0; i < idField.size(); i++) {
			col = ValueUtils.getRequired(colMap.get(idField.get(i)), "不存在的字段" + idField.get(i)+"data:"+colMap);
			psHandle = this.createColumnHandles.get(idField.get(i));
			psHandle.fillPreparedStatementColumnType(preparedStatement, size, col, dataBaseType, resultSetMetaData);
			size++;
		}
		// 设置值
		for (int i = 0; i < updateField.size(); i++) {
			col = ValueUtils.getRequired(colMap.get(updateField.get(i)), "不存在的字段" + updateField.get(i)+"data:"+colMap);
			psHandle = this.createColumnHandles.get(updateField.get(i));
			psHandle.fillPreparedStatementColumnType(preparedStatement, size, col, dataBaseType, resultSetMetaData);
			size++;
		}
		// 设置条件
		byte[] bytes;
		for (int i = 0; i < updateField.size(); i++) {
			col = colMap.get(updateField.get(i));
			psHandle = this.createColumnHandles.get(updateField.get(i));
			if (psHandle.isBigType()) {
				bytes = col.asBytes();
				preparedStatement.setLong(size, bytes == null ? 0L : bytes.length);
			} else {
				psHandle.fillPreparedStatementColumnType(preparedStatement, size, col, dataBaseType, resultSetMetaData);
			}
			size++;
		}
		// insertall
		List<String> lefts = resultSetMetaData.getLeft();
		for (int i = 0; i < lefts.size(); i++) {
			col = colMap.get(lefts.get(i));
			psHandle = this.createColumnHandles.get(lefts.get(i));
			psHandle.fillPreparedStatementColumnType(preparedStatement, size, col, dataBaseType, resultSetMetaData);
			size++;
		}
		return preparedStatement;
	}

}
