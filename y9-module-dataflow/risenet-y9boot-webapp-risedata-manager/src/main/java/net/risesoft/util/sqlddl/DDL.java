package net.risesoft.util.sqlddl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import net.risesoft.y9public.entity.DataTableField;

public class DDL {
	
	/**
	 * 修改表字段
	 * @param dataSource
	 * @param tableName
	 * @param jsonDbColumns
	 * @throws Exception
	 */
	public static void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
		String dbType = DbMetaDataUtil.getDatabaseDialectName(dataSource);
		if (dbType.equalsIgnoreCase("mysql")) {
			DDLmysql.alterTableColumn(dataSource, tableName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("oracle")) {
			DDLoracle.alterTableColumn(dataSource, tableName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("dm")) {
			DDLdm.alterTableColumn(dataSource, tableName, jsonDbColumns);
		}else if (dbType.equalsIgnoreCase("kingbase")) {
			DDLkingbase.alterTableColumn(dataSource, tableName, jsonDbColumns);
		}else if (dbType.equalsIgnoreCase("postgresql")) {
			DDLpg.alterTableColumn(dataSource, tableName, jsonDbColumns);
		}
	}

	/**
	 * 建表
	 * @param dataSource
	 * @param tableName
	 * @param tableCName
	 * @param jsonDbColumns
	 * @throws Exception
	 */
	public static void addTableColumn(DataSource dataSource, String tableName, String tableCName, String jsonDbColumns) throws Exception {
		String dbType = DbMetaDataUtil.getDatabaseDialectName(dataSource);
		if (dbType.equalsIgnoreCase("mysql")) {
			DDLmysql.addTableColumn(dataSource, tableName, tableCName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("oracle")) {
			DDLoracle.addTableColumn(dataSource, tableName, tableCName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("dm")) {
			DDLdm.addTableColumn(dataSource, tableName, tableCName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("kingbase")) {
			DDLkingbase.addTableColumn(dataSource, tableName, tableCName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("postgresql")) {
			DDLpg.addTableColumn(dataSource, tableName, tableCName, jsonDbColumns);
		}
	}

	/**
	 * 重命名表名称
	 * @param dataSource
	 * @param tableNameOld
	 * @param tableNameNew
	 * @throws Exception
	 */
	public static void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
		String dbType = DbMetaDataUtil.getDatabaseDialectName(dataSource);
		if (dbType.equalsIgnoreCase("mysql")) {
			DDLmysql.renameTable(dataSource, tableNameOld, tableNameNew);
		} else if (dbType.equalsIgnoreCase("oracle")) {
			DDLoracle.renameTable(dataSource, tableNameOld, tableNameNew);
		} else if (dbType.equalsIgnoreCase("dm")) {
			DDLdm.renameTable(dataSource, tableNameOld, tableNameNew);
		} else if (dbType.equalsIgnoreCase("kingbase")) {
			DDLkingbase.renameTable(dataSource, tableNameOld, tableNameNew);
		} else if (dbType.equalsIgnoreCase("postgresql")) {
			DDLpg.renameTable(dataSource, tableNameOld, tableNameNew);
		}
	}

	/**
	 * 删除表
	 * @param dataSource
	 * @param tableName
	 * @throws Exception
	 */
	public static void dropTable(DataSource dataSource, String tableName) throws Exception {
		String dbType = DbMetaDataUtil.getDatabaseDialectName(dataSource);
		if (dbType.equalsIgnoreCase("mysql")) {
			DDLmysql.dropTable(dataSource, tableName);
		} else if (dbType.equalsIgnoreCase("oracle")) {
			DDLoracle.dropTable(dataSource, tableName);
		} else if (dbType.equalsIgnoreCase("dm")) {
			DDLdm.dropTable(dataSource, tableName);
		} else if (dbType.equalsIgnoreCase("kingbase")) {
			DDLkingbase.dropTable(dataSource, tableName);
		} else if (dbType.equalsIgnoreCase("postgresql")) {
			DDLpg.dropTable(dataSource, tableName);
		}
	}

	/**
	 * 删除表字段
	 * @param dataSource
	 * @param tableName
	 * @param columnName
	 * @throws Exception
	 */
	public static void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
		String dbType = DbMetaDataUtil.getDatabaseDialectName(dataSource);
		if (dbType.equalsIgnoreCase("mysql")) {
			DDLmysql.dropTableColumn(dataSource, tableName, columnName);
		} else if (dbType.equalsIgnoreCase("oracle")) {
			DDLoracle.dropTableColumn(dataSource, tableName, columnName);
		} else if (dbType.equalsIgnoreCase("dm")) {
			DDLdm.dropTableColumn(dataSource, tableName, columnName);
		} else if (dbType.equalsIgnoreCase("kingbase")) {
			DDLkingbase.dropTableColumn(dataSource, tableName, columnName);
		} else if (dbType.equalsIgnoreCase("postgresql")) {
			DDLpg.dropTableColumn(dataSource, tableName, columnName);
		}
	}
	
	/**
	 * 获取要创建的字段
	 * @param fieldList
	 * @return
	 */
	public static List<DbColumn> getDbColumn(List<DataTableField> fieldList) {
		List<DbColumn> dbcs = new ArrayList<DbColumn>();
		for (DataTableField fieldTemp : fieldList) {
			DbColumn dbColumn = new DbColumn();
			dbColumn.setColumn_name(fieldTemp.getName());
			dbColumn.setColumn_name_old(fieldTemp.getOldName());
			dbColumn.setNullable("YES".equals(fieldTemp.getFieldNull()) ? true : false);
			dbColumn.setIsCreateIndex(false);
			dbColumn.setType_name(fieldTemp.getFieldType());
			dbColumn.setData_type(fieldTemp.getTypeNum());
			//不能有长度的字段置为0
			dbColumn.setData_length(fieldTemp.getFieldLength());
			dbColumn.setComment(fieldTemp.getCname());
			dbColumn.setPrimaryKey("Y".equals(fieldTemp.getFieldPk()) ? true : false);
			dbColumn.setIsState(fieldTemp.getIsState()==null ? false:fieldTemp.getIsState());
			dbcs.add(dbColumn);
		}
		return dbcs;
	}

}
