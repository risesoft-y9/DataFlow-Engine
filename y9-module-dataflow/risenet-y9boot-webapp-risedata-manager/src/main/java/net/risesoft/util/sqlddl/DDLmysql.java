package net.risesoft.util.sqlddl;

import com.fasterxml.jackson.databind.type.TypeFactory;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9.json.Y9JsonUtil;

import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;

public class DDLmysql {

	/**
	 * 修改表字段
	 * @param dataSource
	 * @param tableName
	 * @param jsonDbColumns
	 * @throws Exception
	 */
	public static void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
		if (!DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
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
			DbMetaDataUtil.executeDDL(dataSource, DDL);
		}
	}

	/**
	 * 新建表
	 * @param dataSource
	 * @param tableName
	 * @param jsonDbColumns
	 * @throws Exception
	 */
	public static void addTableColumn(DataSource dataSource, String tableName, String tableCName, String jsonDbColumns) throws Exception {
		DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns, TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
		if (DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
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
				DbMetaDataUtil.executeDDL(dataSource, DDL);
				
				if (dbc.getIsCreateIndex() && !dbc.getIsState()) {
					String str = "ALTER TABLE " + tableName + " ADD INDEX " + Y9IdGenerator.genId(IdType.UUID) + " (" + dbc.getColumn_name() + ")";
					DbMetaDataUtil.executeDDL(dataSource, str);
				}
			}
		} else {//table不存在。
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE " + tableName + " (\r\n");
			String isPK = "";
			for (DbColumn dbc : dbcs) {
				String key = Y9IdGenerator.genId(IdType.UUID);
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
					sb.append("KEY "+key+" ("+ columnName +") USING BTREE");
					sb.append(",\r\n");
				}
			}
			sb.append(isPK).append(")");
			
			if(StringUtils.isNotBlank(tableCName)) {
				sb.append(" COMMENT='" + tableCName + "'");
			}
			
			DbMetaDataUtil.executeDDL(dataSource, sb.toString());
		}
	}

	public static void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
		DbMetaDataUtil.executeDDL(dataSource, "ALTER TABLE " + tableNameOld + " RENAME " + tableNameNew);
	}

	public static void dropTable(DataSource dataSource, String tableName) throws Exception {
		DbMetaDataUtil.executeDDL(dataSource, "DROP TABLE " + tableName);
	}

	public static void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
		DbMetaDataUtil.executeDDL(dataSource, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
	}

}
