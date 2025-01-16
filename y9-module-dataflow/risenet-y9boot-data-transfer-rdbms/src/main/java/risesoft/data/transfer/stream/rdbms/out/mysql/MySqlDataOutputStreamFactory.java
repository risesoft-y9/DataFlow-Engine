package risesoft.data.transfer.stream.rdbms.out.mysql;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.stream.out.DataOutputStream;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.stream.rdbms.out.RdbmsDataOutputStreamFactory;
import risesoft.data.transfer.stream.rdbms.utils.DBUtil;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * mysql 数据输出流主要是update 语法不一致
 * 
 * @typeName MySqlDataOutputStreamFactory
 * @date 2024年5月10日
 * @author lb
 */
public class MySqlDataOutputStreamFactory extends RdbmsDataOutputStreamFactory {

	public MySqlDataOutputStreamFactory(Configuration configuration, JobContext jobContext) {
		super(configuration, jobContext);
	}

	@Override
	protected void createUpdate(int size) {
		idField = Arrays.asList(getStrings(writerType, "update"));
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
		sb.append(" ON DUPLICATE KEY UPDATE ");
		String newDatabaseField;
		for (int i = 0; i < updateField.size(); i++) {
			newDatabaseField = DataBaseType.castKeyField(databaseKeys, databaseKeyMeaning, updateField.get(i));
			if (i != 0) {
				sb.append(",");
			}
			sb.append(newDatabaseField + " = values(" + newDatabaseField + ")");
		}
		this.workSql = sb.toString();
		if (logger.isInfo()) {
			logger.info(this, "mysql worksql:" + this.workSql);
		}
	}

	protected void createReplace(int size) {
		createUpdate(size);
	}

	@Override
	protected DataOutputStream getUpdateStream() {
		return new MySqlUpadateDataOutputStream(DBUtil.getConnection(dataBaseType, jdbcUrl, userName, password),
				workSql, resultSetMetaData, createColumnHandles, dataBaseType, logger);
	}

}
