package risesoft.data.transfer.stream.rdbms.out.pg;

import java.util.ArrayList;
import java.util.Arrays;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.stream.out.DataOutputStream;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.stream.rdbms.out.RdbmsDataOutputStreamFactory;
import risesoft.data.transfer.stream.rdbms.utils.DBUtil;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * PG 数据输出流主要是update 语法不一致
 * 
 * @typeName PGDataOutputStreamFactory
 * @date 2024年5月10日
 * @author lb
 */
public class PGDataOutputStreamFactory extends RdbmsDataOutputStreamFactory {

	public PGDataOutputStreamFactory(Configuration configuration, JobContext jobContext) {
		super(configuration, jobContext);
	}

	@Override
	protected void createUpdate(int size) {
		idField = Arrays.asList(getStrings(writerType, "update"));
		createSql(size);
	}

	@Override
	protected void createReplace(int size) {
		idField = Arrays.asList(getStrings(writerType, "replace"));
		createSql(size);
	}
	
	private void createSql(int size) {
		updateField = new ArrayList<String>();
		for (String columnHolder : resultSetMetaData.getLeft()) {
			if (!idField.contains(columnHolder)) {
				updateField.add(columnHolder);
			}
		}
		StringBuilder sb = new StringBuilder("insert into ").append(tableName).append(" (")
				.append(DataBaseType.castKeyFieldsAndJoin(dataBaseType, this.resultSetMetaData.getLeft(), ","))
				.append(") values (");
		for (int i = 0; i < size; i++) {
			sb.append("?");
			if (i != size - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		sb.append(" ON CONFLICT (" + DataBaseType.castKeyFieldsAndJoin(dataBaseType, idField, ",") + ") do update set ");
		for (int i = 0; i < updateField.size(); i++) {
			if (i != 0) {
				sb.append(",");
			}
			// cast
			sb.append(DataBaseType.castKeyField(databaseKeys, databaseKeyMeaning, updateField.get(i)) + " = ?");
		}
//		sb.append(" DO NOTHING");
		this.workSql = sb.toString();
		if (logger.isInfo()) {
			logger.info(this, "pg worksql:" + this.workSql);
		}
	}

	@Override
	protected DataOutputStream getUpdateStream() {
		return new PGUpadateDataOutputStream(DBUtil.getConnection(dataBaseType, jdbcUrl, userName, password), workSql,
				resultSetMetaData, createColumnHandles, dataBaseType, logger, updateField);
	}
	
	@Override
	protected DataOutputStream getReplaceStream() {
		return new PGUpadateDataOutputStream(DBUtil.getConnection(dataBaseType, jdbcUrl, userName, password), workSql,
				resultSetMetaData, createColumnHandles, dataBaseType, logger, updateField);
	}

}
