package net.risesoft.util.sqlddl;

import com.fasterxml.jackson.databind.type.TypeFactory;

import net.risesoft.y9.json.Y9JsonUtil;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

public class DDLdm {
	
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
						DbMetaDataUtil.executeDDL(connection, sb.append(" RENAME COLUMN " + dbc.getColumn_name_old() + " TO " + dbc.getColumn_name()).toString(), false);
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

				DbMetaDataUtil.executeDDL(connection, sb.toString(), false);
				
				if (StringUtils.isNotBlank(dbc.getComment())) {
					DbMetaDataUtil.executeDDL(connection, "COMMENT ON COLUMN " + tableName.trim().toUpperCase() + "." + dbc.getColumn_name().trim().toUpperCase() + " IS '" + dbc.getComment() + "'", false);
				}
			}
		}
		DbMetaDataUtil.ReleaseResource(connection, null, null, null);
	}

	/**
	 * 新建表
	 * @param dataSource
	 * @param tableName
	 * @param jsonDbColumns
	 * @throws Exception
	 */
	public static void addTableColumn(Connection connection, String tableName, String tableCName, String jsonDbColumns) throws Exception {
		DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns, TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
		if (DbMetaDataUtil.checkTableExist(connection, tableName, false)) {
			for (DbColumn dbc : dbcs) {
				String columnName = dbc.getColumn_name();
				String DDL = "ALTER TABLE " + tableName;
				if(!dbc.getIsState()) {//没有则创建
					DDL += " ADD " + dbc.getColumn_name() + " ";
				}else {//有则判断是否是修改
					if (dbc.getColumn_name().equalsIgnoreCase(dbc.getColumn_name_old())||StringUtils.isBlank(dbc.getColumn_name_old())) { //字段名称没有改变
						DDL += " MODIFY " + dbc.getColumn_name() + " ";
					} else {
						DDL += " CHANGE " + dbc.getColumn_name_old() + " " + dbc.getColumn_name() + " ";
					}
				}
				
				String sType = dbc.getType_name().toUpperCase();
				if (StringUtils.isNotBlank(dbc.getData_length())) {
					DDL += sType + "(" + dbc.getData_length() + ")";
				} else {
					DDL += sType;
				}
				
				if (dbc.getNullable()) {
					DDL += " DEFAULT NULL";
				} else {
					DDL += " NOT NULL";
				}
				
				DbMetaDataUtil.executeDDL(connection, DDL, false);
				
				if (StringUtils.isNotBlank(dbc.getComment())) {
					DbMetaDataUtil.executeDDL(connection, "COMMENT ON COLUMN " + tableName.trim().toUpperCase() + "." + columnName.trim().toUpperCase() + " IS '" + "\""+dbc.getColumn_name()+"\"" + "'", false);
				}
				
				if (dbc.getIsCreateIndex() && !dbc.getIsState()) {
					DbMetaDataUtil.executeDDL(connection, "CREATE INDEX \"" + tableName+"_"+columnName + "\" ON " + tableName + " (" + columnName + " )", false);
				}
			}
			DbMetaDataUtil.ReleaseResource(connection, null, null, null);
		} else { //table不存在。
			StringBuilder sb = new StringBuilder();
			String isPK = "";
			sb.append("CREATE TABLE " + tableName + " (\r\n");
			for (DbColumn dbc : dbcs) {
				String columnName = dbc.getColumn_name();
				if (dbc.getPrimaryKey()){
					isPK += "PRIMARY KEY ("+dbc.getColumn_name()+") \r\n";
				}

				sb.append(columnName).append(" ");
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
			DbMetaDataUtil.executeDDL(connection, sb.toString(), false);
			
			if(StringUtils.isNotBlank(tableCName)) {
				DbMetaDataUtil.executeDDL(connection, "COMMENT ON TABLE " + tableName.trim() +" IS '" + tableCName + "'", false);
			}

			for (DbColumn dbc : dbcs) {
				String columnName = dbc.getColumn_name();
				if (StringUtils.isNotBlank(dbc.getComment())) {
					DbMetaDataUtil.executeDDL(connection, "COMMENT ON COLUMN " + tableName.trim() + "." + columnName.trim() + " IS '" + dbc.getComment() + "'", false);
				}
				if (dbc.getIsCreateIndex()) {
					DbMetaDataUtil.executeDDL(connection, "CREATE INDEX \"" + tableName+"_"+columnName + "\" ON " + tableName + " (" + columnName + " )", false);
				}
			}
			DbMetaDataUtil.ReleaseResource(connection, null, null, null);
		}
	}
	
	public static void renameTable(Connection connection, String tableNameOld, String tableNameNew) throws Exception {
		DbMetaDataUtil.executeDDL(connection, "ALTER TABLE " + tableNameOld + " RENAME TO " + tableNameNew, true);
	}

	public static void dropTableColumn(Connection connection, String tableName, String columnName) throws Exception {
		DbMetaDataUtil.executeDDL(connection, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName, true);
	}

	public static void dropTable(Connection connection, String tableName) throws Exception {
		DbMetaDataUtil.executeDDL(connection, "DROP TABLE " + tableName, true);
	}

}
