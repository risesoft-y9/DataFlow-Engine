package net.risesoft.util.sqlddl;

import com.fasterxml.jackson.databind.type.TypeFactory;

import net.risesoft.y9.json.Y9JsonUtil;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

public class DDLmysql {

	/**
	 * 修改表字段
	 * @param connection
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
			String DDL = "ALTER TABLE " + tableName;
			if (dbc.getColumn_name().equalsIgnoreCase(dbc.getColumn_name_old())) { //字段名称没有改变
				DDL += " MODIFY COLUMN " + dbc.getColumn_name() + " ";
			} else {
				DDL += " CHANGE COLUMN " + dbc.getColumn_name_old() + " " + dbc.getColumn_name() + " ";
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
			if (StringUtils.isNotBlank(dbc.getComment())) {
				DDL += " COMMENT '" + dbc.getComment() + "'";
			}
			DbMetaDataUtil.executeDDL(connection, DDL, false);
		}
		DbMetaDataUtil.ReleaseResource(connection, null, null, null);
	}

	/**
	 * 新建表
	 * @param connection
	 * @param tableName
	 * @param jsonDbColumns
	 * @throws Exception
	 */
	public static void addTableColumn(Connection connection, String tableName, String tableCName, String jsonDbColumns) throws Exception {
		DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns, TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
		if (DbMetaDataUtil.checkTableExist(connection, tableName, false)) {
			for (DbColumn dbc : dbcs) {
				String DDL = "ALTER TABLE " + tableName;
				if(!dbc.getIsState()) {//没有则创建
					DDL += " ADD COLUMN " + dbc.getColumn_name() + " ";
				}else {//有则判断是否是修改
					if (dbc.getColumn_name().equalsIgnoreCase(dbc.getColumn_name_old())||StringUtils.isBlank(dbc.getColumn_name_old())) { //字段名称没有改变
						DDL += " MODIFY COLUMN " + dbc.getColumn_name() + " ";
					} else {
						DDL += " CHANGE COLUMN " + dbc.getColumn_name_old() + " " + dbc.getColumn_name() + " ";
					}
				}

				String sType = dbc.getType_name().toUpperCase();
				if(StringUtils.isNotBlank(dbc.getData_length())){
					DDL += sType + "(" + dbc.getData_length() + ")";
				}else {
					DDL += sType;
				}

				if (dbc.getNullable()) {
					DDL += " DEFAULT NULL";
				} else {
					DDL += " NOT NULL";
				}
				if (StringUtils.isNotBlank(dbc.getComment())) {
					DDL += " COMMENT '" + dbc.getComment() + "'";
				}
				DbMetaDataUtil.executeDDL(connection, DDL, false);
				
				if (dbc.getIsCreateIndex() && !dbc.getIsState()) {
					String str = "ALTER TABLE " + tableName + " ADD INDEX " + tableName + "_" + dbc.getColumn_name() + " (" + dbc.getColumn_name() + ")";
					DbMetaDataUtil.executeDDL(connection, str, false);
				}
			}
			DbMetaDataUtil.ReleaseResource(connection, null, null, null);
		} else {//table不存在。
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE " + tableName + " (\r\n");
			String isPK = "";
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
				if (StringUtils.isNotBlank(dbc.getComment())) {
					sb.append(" COMMENT '" + dbc.getComment() + "'");
				}
				sb.append(",\r\n");
				if (dbc.getIsCreateIndex()) {
					sb.append("KEY " + tableName + "_" + columnName + " ("+ columnName +") USING BTREE");
					sb.append(",\r\n");
				}
			}
			sb.append(isPK).append(")");
			
			if(StringUtils.isNotBlank(tableCName)) {
				sb.append(" COMMENT='" + tableCName + "'");
			}
			
			DbMetaDataUtil.executeDDL(connection, sb.toString(), true);
		}
	}

	public static void renameTable(Connection connection, String tableNameOld, String tableNameNew) throws Exception {
		DbMetaDataUtil.executeDDL(connection, "ALTER TABLE " + tableNameOld + " RENAME " + tableNameNew, true);
	}

	public static void dropTable(Connection connection, String tableName) throws Exception {
		DbMetaDataUtil.executeDDL(connection, "DROP TABLE " + tableName, true);
	}

	public static void dropTableColumn(Connection connection, String tableName, String columnName) throws Exception {
		DbMetaDataUtil.executeDDL(connection, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName, true);
	}

}
