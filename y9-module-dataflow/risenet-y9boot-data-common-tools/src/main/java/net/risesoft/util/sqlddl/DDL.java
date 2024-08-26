package net.risesoft.util.sqlddl;

import javax.sql.DataSource;

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
	
}
