package net.risesoft.util.sqlddl;

import com.fasterxml.jackson.databind.type.TypeFactory;

import net.risesoft.y9.json.Y9JsonUtil;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

public class DDLoracle {
	
	/**
	 * 修改表字段
	 * @param dataSource
	 * @param tableName
	 * @param jsonDbColumns
	 * @throws Exception
	 */
	public static void alterTableColumn(Connection connection, String tableName, String jsonDbColumns) throws Exception {
		if (!DbMetaDataUtil.checkTableExist(connection, tableName, false)) {
			DbMetaDataUtil.ReleaseResource(connection, null, null, null);
			throw new Exception("数据库中不存在这个表：" + tableName);
		}
		DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns, TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
		for (DbColumn dbc : dbcs) {
			if (StringUtils.isNotBlank(dbc.getColumn_name_old())) {
				StringBuilder sb = new StringBuilder();
				sb.append("ALTER TABLE " + tableName);
				if (!dbc.getColumn_name().equalsIgnoreCase(dbc.getColumn_name_old())) { //字段名称有改变
					try {
						DbMetaDataUtil.executeDDL(connection, sb.append(" RENAME COLUMN " + dbc.getColumn_name_old() + " TO " + dbc.getColumn_name()).toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				sb.append(" MODIFY " + dbc.getColumn_name() + " ");

				String sType = dbc.getType_name().toUpperCase();
				if (StringUtils.isNotBlank(dbc.getData_length())) {
					sb.append(sType + "(" + dbc.getData_length() + ")");
				} else {
					sb.append(sType);
				}

				if (dbc.getNullable()) {
					sb.append(" NULL");
				} else {
					sb.append(" NOT NULL");
				}
				DbMetaDataUtil.executeDDL(connection, sb.toString());

				if (StringUtils.isNotBlank(dbc.getComment())) {
					DbMetaDataUtil.executeDDL(connection, "COMMENT ON COLUMN " + tableName.trim().toUpperCase() + "." + dbc.getColumn_name().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
				}
			}
		}
	}

	/**
	 * 新建表
	 * @param dataSource
	 * @param tableName
	 * @param tableCName
	 * @param jsonDbColumns
	 * @throws Exception
	 */
	public static void addTableColumn(Connection connection, String tableName, String tableCName, String jsonDbColumns) throws Exception {
		DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns, TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
		if (DbMetaDataUtil.checkTableExist(connection, tableName, false)) {
			for (DbColumn dbc : dbcs) {
				String columnName = "\""+dbc.getColumn_name()+"\"";
				String DDL = "ALTER TABLE " + tableName;
				if(!dbc.getIsState()) {//没有则创建
					DDL += " ADD " + columnName + " ";
				}else {//有则判断是否是修改
					if (columnName.equalsIgnoreCase(dbc.getColumn_name_old())||StringUtils.isBlank(dbc.getColumn_name_old())) { //字段名称没有改变
						DDL += " MODIFY " + columnName + " ";
					} else {
						DDL += " CHANGE " + dbc.getColumn_name_old() + " " + columnName + " ";
					}
				}

				String sType = dbc.getType_name().toUpperCase();
				if (StringUtils.isNotBlank(dbc.getData_length())) {
					DDL += sType + "(" + dbc.getData_length() + ")";
				} else {
					DDL += sType;
				}

				if (!dbc.getNullable()&&!dbc.getIsState()) {
					DDL += " NOT NULL ";
				}

				DbMetaDataUtil.executeDDL(connection, DDL);
				if (StringUtils.isNotBlank(dbc.getComment())) {
					DbMetaDataUtil.executeDDL(connection, "COMMENT ON COLUMN " + tableName.trim().toUpperCase() + "." + columnName.trim().toUpperCase() + " IS '" + "\""+dbc.getColumn_name()+"\"" + "'");
				}

				if (dbc.getIsCreateIndex() && !dbc.getIsState()) {
					DbMetaDataUtil.executeDDL(connection, "CREATE INDEX \"" + tableName+"_"+columnName + "\" ON " + tableName + " (" + columnName + " )");
				}
			}
		} else { //table不存在。
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE " + tableName + " (\r\n");

			String isPK = "";
			for (DbColumn dbc : dbcs) {
				String columnName = dbc.getColumn_name();
				if (dbc.getPrimaryKey()){
					isPK += "PRIMARY KEY (\""+dbc.getColumn_name()+"\") \r\n";
				}
				sb.append("\""+columnName+"\"").append(" ");
				String sType = dbc.getType_name().toUpperCase();
				if (StringUtils.isNotBlank(dbc.getData_length())) {
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
			DbMetaDataUtil.executeDDL(connection, sb.toString());

			if(StringUtils.isNotBlank(tableCName)) {
				DbMetaDataUtil.executeDDL(connection, "COMMENT ON TABLE " + tableName.trim() +" IS '" + tableCName + "'");
			}
			
			for (DbColumn dbc : dbcs) {
				String columnName = "\""+dbc.getColumn_name()+"\"";
				if (StringUtils.isNotBlank(dbc.getComment())) {
					DbMetaDataUtil.executeDDL(connection, "COMMENT ON COLUMN " + tableName.trim() + "." + columnName.trim() + " IS '" + dbc.getComment() + "'");
				}
				if (dbc.getIsCreateIndex()) {
					DbMetaDataUtil.executeDDL(connection, "CREATE INDEX " + tableName.trim()+"_"+columnName + " ON " + tableName + " (" + columnName + " )");
				}
			}
		}
	}

	public static void renameTable(Connection connection, String tableNameOld, String tableNameNew) throws Exception {
		DbMetaDataUtil.executeDDL(connection, "RENAME " + tableNameOld + " TO " + tableNameNew);
	}

	public static void dropTableColumn(Connection connection, String tableName, String columnName) throws Exception {
		DbMetaDataUtil.executeDDL(connection, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
	}

	public static void dropTable(Connection connection, String tableName) throws Exception {
		DbMetaDataUtil.executeDDL(connection, "DROP TABLE " + tableName);
	}
}
