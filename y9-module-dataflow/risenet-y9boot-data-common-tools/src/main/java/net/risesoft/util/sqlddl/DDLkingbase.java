package net.risesoft.util.sqlddl;

import com.fasterxml.jackson.databind.type.TypeFactory;
import net.risesoft.y9.json.Y9JsonUtil;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;

public class DDLkingbase {

	/**
	 * 新建表
	 * 
	 * @param dataSource
	 * @param tableName
	 * @param jsonDbColumns
	 * @throws Exception
	 */
	public static void addTableColumn(DataSource dataSource, String tableName, String tableCName, String jsonDbColumns) throws Exception {
		DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns, TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
		if (DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
			for (DbColumn dbc : dbcs) {
				String columnName = dbc.getColumn_name();
				String DDL = "ALTER TABLE " + tableName;
				if (!dbc.getIsState()) {// 没有则创建
					DDL += " ADD COLUMN " + dbc.getColumn_name() + " ";
				} else {// 有则判断是否是修改
					if (dbc.getColumn_name().equalsIgnoreCase(dbc.getColumn_name_old())
							|| StringUtils.isBlank(dbc.getColumn_name_old())) { // 字段名称没有改变
						DDL += " ALTER COLUMN " + dbc.getColumn_name() + " TYPE ";
					} else {
						DDL += " RENAME " + dbc.getColumn_name_old() + " TO " + dbc.getColumn_name() + " ";
						DDL += "ALTER TABLE " + tableName;
						DDL += " ALTER COLUMN " + columnName + " TYPE ";
					}
				}

				String sType = dbc.getType_name().toUpperCase();
				if (StringUtils.isNotBlank(dbc.getData_length()) && !"0".equals(dbc.getData_length())) {
					DDL += sType + "(" + dbc.getData_length() + ");";
				} else {
					DDL += sType + ";";
				}

				DDL += "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName;
				if (dbc.getNullable() == true) {
					DDL += " DROP NOT NULL;";
				} else {
					DDL += " SET NOT NULL;";
				}

				DbMetaDataUtil.executeDDL(dataSource, DDL);
				if (StringUtils.isNotBlank(dbc.getComment())) {
					DbMetaDataUtil.executeDDL(dataSource, "COMMENT ON COLUMN " + tableName.trim().toUpperCase() + "."
							+ dbc.getColumn_name().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
				}

				if (dbc.getIsCreateIndex() && !dbc.getIsState()) {
					DbMetaDataUtil.executeDDL(dataSource,
							"CREATE INDEX \"" + tableName + "_" + dbc.getColumn_name() + "\" ON " + tableName + " USING BTREE(" + columnName + " )");
				}
			}
		} else { // table不存在。
			StringBuilder sb = new StringBuilder();
			String isPK = "";
			sb.append("CREATE TABLE " + tableName + " (\r\n");
			for (DbColumn dbc : dbcs) {
				String columnName = dbc.getColumn_name();
				if (dbc.getPrimaryKey()) {
					isPK += "PRIMARY KEY (\"" + dbc.getColumn_name() + "\") \r\n";
				}

				sb.append(columnName).append(" ");
				String sType = dbc.getType_name().toUpperCase();
				if (StringUtils.isNotBlank(dbc.getData_length()) && !"0".equals(dbc.getData_length())) {
					sb.append(sType + "(" + dbc.getData_length() + ")");
				} else {
					sb.append(sType);
				}

				if (!dbc.getNullable()) {
					sb.append(" NOT NULL");
				}
				sb.append(",\r\n");
			}

			sb.append(isPK).append(")");
			DbMetaDataUtil.executeDDL(dataSource, sb.toString());
			
			if(StringUtils.isNotBlank(tableCName)) {
				DbMetaDataUtil.executeDDL(dataSource,"COMMENT ON TABLE " + tableName.trim() +" IS '" + tableCName + "'");
			}

			for (DbColumn dbc : dbcs) {
				String columnName = dbc.getColumn_name();
				if (StringUtils.isNotBlank(dbc.getComment())) {
					DbMetaDataUtil.executeDDL(dataSource, "COMMENT ON COLUMN " + tableName.trim().toUpperCase() + "."
							+ dbc.getColumn_name().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
				}
				if (dbc.getIsCreateIndex()) {
					DbMetaDataUtil.executeDDL(dataSource, "CREATE INDEX \"" + tableName + "_" + dbc.getColumn_name() + "\" ON " + tableName + " USING BTREE(" + columnName + " )");
				}
			}
		}
	}

	public static void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
		DbMetaDataUtil.executeDDL(dataSource, "ALTER TABLE " + tableNameOld + " RENAME TO " + tableNameNew);
	}

	public static void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
		DbMetaDataUtil.executeDDL(dataSource, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
	}

	public static void dropTable(DataSource dataSource, String tableName) throws Exception {
		DbMetaDataUtil.executeDDL(dataSource, "DROP TABLE " + tableName);
	}

	public static void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns)
			throws Exception {
		if (!DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
			throw new Exception("数据库中不存在这个表：" + tableName);
		}
		DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
				TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
		for (DbColumn dbc : dbcs) {
			String columnName = dbc.getColumn_name();
			String oldColumnName = dbc.getColumn_name_old();
			boolean isModify = true;
			if (columnName.equalsIgnoreCase(oldColumnName) || StringUtils.isBlank(oldColumnName)) {
				isModify = false;
			}
			String DDL = "ALTER TABLE " + tableName;

			if (isModify) {
				DDL += " RENAME COLUMN " + oldColumnName + " TO " + columnName + "; ";
				DDL += "ALTER TABLE " + tableName;
				DDL += " ALTER COLUMN " + columnName + " TYPE ";

			} else {
				DDL += " ALTER COLUMN " + columnName + " TYPE ";

			}
			String sType = dbc.getType_name().toUpperCase();// TYPE varchar(255)
			if (StringUtils.isNotBlank(dbc.getData_length()) && !"0".equals(dbc.getData_length())) {
				DDL += sType + "(" + dbc.getData_length() + "); ";
			} else {
				DDL += sType + "; ";
			}
			DDL += "ALTER TABLE " + tableName + " ALTER COLUMN " + columnName;
			if (dbc.getNullable() == true) {
				DDL += " DROP NOT NULL;";
			} else {
				DDL += " SET NOT NULL;";
			}
			if (dbc.getComment().length() > 0) {
				DDL += " COMMENT ON  COLUMN " + tableName + "." + columnName + " IS '" + dbc.getComment() + "'";
			}
			DbMetaDataUtil.executeDDL(dataSource, DDL);
		}
	}

}
