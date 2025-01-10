package net.risesoft.util.sqlddl;

import java.sql.Connection;

public class DDL {
	
	/**
	 * 修改表字段
	 * @param connection
	 * @param tableName
	 * @param jsonDbColumns
	 * @throws Exception
	 */
	public static void alterTableColumn(Connection connection, String tableName, String jsonDbColumns) throws Exception {
		String dbType = DbMetaDataUtil.getDatabaseDialectName(connection, false);
		if (dbType.equalsIgnoreCase("mysql")) {
			DDLmysql.alterTableColumn(connection, tableName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("oracle")) {
			DDLoracle.alterTableColumn(connection, tableName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("dm")) {
			DDLdm.alterTableColumn(connection, tableName, jsonDbColumns);
		}else if (dbType.equalsIgnoreCase("kingbase")) {
			DDLkingbase.alterTableColumn(connection, tableName, jsonDbColumns);
		}else if (dbType.equalsIgnoreCase("postgresql")) {
			DDLpg.alterTableColumn(connection, tableName, jsonDbColumns);
		}
	}

	/**
	 * 建表
	 * @param connection
	 * @param tableName
	 * @param tableCName
	 * @param jsonDbColumns
	 * @throws Exception
	 */
	public static void addTableColumn(Connection connection, String tableName, String tableCName, String jsonDbColumns) throws Exception {
		String dbType = DbMetaDataUtil.getDatabaseDialectName(connection, false);
		if (dbType.equalsIgnoreCase("mysql")) {
			DDLmysql.addTableColumn(connection, tableName, tableCName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("oracle")) {
			DDLoracle.addTableColumn(connection, tableName, tableCName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("dm")) {
			DDLdm.addTableColumn(connection, tableName, tableCName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("kingbase")) {
			DDLkingbase.addTableColumn(connection, tableName, tableCName, jsonDbColumns);
		} else if (dbType.equalsIgnoreCase("postgresql")) {
			DDLpg.addTableColumn(connection, tableName, tableCName, jsonDbColumns);
		}
	}

	/**
	 * 重命名表名称
	 * @param connection
	 * @param tableNameOld
	 * @param tableNameNew
	 * @throws Exception
	 */
	public static void renameTable(Connection connection, String tableNameOld, String tableNameNew) throws Exception {
		String dbType = DbMetaDataUtil.getDatabaseDialectName(connection, false);
		if (dbType.equalsIgnoreCase("mysql")) {
			DDLmysql.renameTable(connection, tableNameOld, tableNameNew);
		} else if (dbType.equalsIgnoreCase("oracle")) {
			DDLoracle.renameTable(connection, tableNameOld, tableNameNew);
		} else if (dbType.equalsIgnoreCase("dm")) {
			DDLdm.renameTable(connection, tableNameOld, tableNameNew);
		} else if (dbType.equalsIgnoreCase("kingbase")) {
			DDLkingbase.renameTable(connection, tableNameOld, tableNameNew);
		} else if (dbType.equalsIgnoreCase("postgresql")) {
			DDLpg.renameTable(connection, tableNameOld, tableNameNew);
		}
	}

	/**
	 * 删除表
	 * @param connection
	 * @param tableName
	 * @throws Exception
	 */
	public static void dropTable(Connection connection, String tableName) throws Exception {
		String dbType = DbMetaDataUtil.getDatabaseDialectName(connection, false);
		if (dbType.equalsIgnoreCase("mysql")) {
			DDLmysql.dropTable(connection, tableName);
		} else if (dbType.equalsIgnoreCase("oracle")) {
			DDLoracle.dropTable(connection, tableName);
		} else if (dbType.equalsIgnoreCase("dm")) {
			DDLdm.dropTable(connection, tableName);
		} else if (dbType.equalsIgnoreCase("kingbase")) {
			DDLkingbase.dropTable(connection, tableName);
		} else if (dbType.equalsIgnoreCase("postgresql")) {
			DDLpg.dropTable(connection, tableName);
		}
	}

	/**
	 * 删除表字段
	 * @param connection
	 * @param tableName
	 * @param columnName
	 * @throws Exception
	 */
	public static void dropTableColumn(Connection connection, String tableName, String columnName) throws Exception {
		String dbType = DbMetaDataUtil.getDatabaseDialectName(connection, false);
		if (dbType.equalsIgnoreCase("mysql")) {
			DDLmysql.dropTableColumn(connection, tableName, columnName);
		} else if (dbType.equalsIgnoreCase("oracle")) {
			DDLoracle.dropTableColumn(connection, tableName, columnName);
		} else if (dbType.equalsIgnoreCase("dm")) {
			DDLdm.dropTableColumn(connection, tableName, columnName);
		} else if (dbType.equalsIgnoreCase("kingbase")) {
			DDLkingbase.dropTableColumn(connection, tableName, columnName);
		} else if (dbType.equalsIgnoreCase("postgresql")) {
			DDLpg.dropTableColumn(connection, tableName, columnName);
		}
	}
	
}
