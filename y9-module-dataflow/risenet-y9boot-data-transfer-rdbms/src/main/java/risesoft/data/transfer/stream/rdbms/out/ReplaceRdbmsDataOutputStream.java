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
 * Replace 操作的流 没有逻辑判断直接根据id修改数据
 * 
 * @typeName ReplaceRdbmsDataOutputStream
 * @date 2023年12月21日
 * @author lb
 */
public class ReplaceRdbmsDataOutputStream extends RdbmsDataOutputStream {
	/**
	 * id 字段
	 */
	private List<String> idField;
	/**
	 * 修改的字段
	 */
	private List<String> updateField;

	public ReplaceRdbmsDataOutputStream(Connection connection, String workSql,
			Triple<List<String>, List<Integer>, List<String>> resultSetMetaData,
			Map<String, PreparedStatementHandle> createCloumnHandles, DataBaseType dataBaseType, List<String> idField,
			List<String> updateField,Logger logger) {
		super(connection, workSql, resultSetMetaData, createCloumnHandles, dataBaseType,logger);
		this.idField = idField;
		this.updateField = updateField;
	}

	protected PreparedStatement fillPreparedStatement(PreparedStatement preparedStatement, Record record)
			throws Exception {
		int size = 1;
		Map<String, Column> colMap = getRecordMap(record);
		Column col;
		PreparedStatementHandle psHandle;
		for (int i = 0; i < updateField.size(); i++) {
			col = ValueUtils.getRequired(colMap.get(updateField.get(i)), "不存在的字段:" + updateField.get(i));
			psHandle = this.createCloumnHandles.get(updateField.get(i));
			psHandle.fillPreparedStatementColumnType(preparedStatement, size, col, dataBaseType, resultSetMetaData);
			size++;
		}
		for (int i = 0; i < idField.size(); i++) {
			col = ValueUtils.getRequired(colMap.get(idField.get(i)), "不存在的字段:" + idField.get(i));
			psHandle = this.createCloumnHandles.get(idField.get(i));
			psHandle.fillPreparedStatementColumnType(preparedStatement, size, col, dataBaseType, resultSetMetaData);
			size++;
		}
		return preparedStatement;
	}

}
