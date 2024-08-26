package net.risesoft.util.sqlddl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class SqlPaginationUtil {
	private static Logger log = LoggerFactory.getLogger(SqlPaginationUtil.class);

	private static String dbType;
	private static int dbVersion;

	public static String generatePagedSql(String databaseType, int databaseVersion, String sql, int start, int limit) throws Exception {
		String rSql = "";
		if (limit == 0) {
			limit = Integer.MAX_VALUE;
		}

		if (databaseType.equalsIgnoreCase("mysql")) {
			rSql = sql + " limit " + start + "," + limit;
		}else if (databaseType.equalsIgnoreCase("mssql")) {
			if (databaseVersion >= 12) {
				if (sql.toLowerCase().contains(" order by ")) {
					//只适用mssql2012版本
					rSql = sql + " OFFSET " + start + " ROW FETCH NEXT " + limit + " rows only";
				} else {
					rSql = "SELECT TOP " + limit + " A.* FROM ( SELECT ROW_NUMBER() OVER (ORDER BY (select NULL)) AS RowNumber,B.* FROM ( " + sql + ") B ) A WHERE A.RowNumber > " + start;
				}
			} else {
				rSql = "SELECT TOP " + limit + " A.* FROM ( SELECT ROW_NUMBER() OVER (ORDER BY (select NULL)) AS RowNumber,B.* FROM ( " + sql + ") B ) A WHERE A.RowNumber > " + start;
			}
		}else if (databaseType.equalsIgnoreCase("oracle")) {
			rSql = "select * from (select mytable.*,rownum as my_rownum from (" + sql + ") mytable) where my_rownum<=" + (start + limit) + " and my_rownum>" + start;
		}else if(databaseType.equalsIgnoreCase("postgre")) {
			rSql = sql + " limit " + start + " offset " + limit;
		}else if(databaseType.equalsIgnoreCase("kingbase")) {
			rSql = sql + " limit " + start + "," + limit;
		}else {
			rSql = sql + " limit " + start + "," + limit;
		}

		return rSql;
	}

	public static String generatePagedSql(DataSource ds, String sql, int start, int limit) throws Exception {
		String rSql = "";
		if (limit == 0) {
			limit = Integer.MAX_VALUE;
		}

		if (dbType == null) {
			Connection connection = null;
			try {
				connection = ds.getConnection();
				DatabaseMetaData dbmd = connection.getMetaData();
				String databaseName = dbmd.getDatabaseProductName().toLowerCase();
				if (databaseName.indexOf("mysql") > -1) {
					dbType = "mysql";
				} else if (databaseName.indexOf("oracle") > -1) {
					dbType = "oracle";
				} else if (databaseName.indexOf("microsoft") > -1) {
					dbType = "mssql";
				} else if(databaseName.indexOf("postgre") > -1) {
					dbType = "postgre";
				} else if(databaseName.indexOf("kingbase") > -1) {
					dbType = "kingbase";
				}

				dbVersion = dbmd.getDatabaseMajorVersion();
			} catch (SQLException e) {
				log.error(e.getMessage());
			} finally {
				try {
					connection.close();
				} catch (SQLException e) {
					log.error(e.getMessage());
				}
			}
		}

		rSql = generatePagedSql(dbType, dbVersion, sql, start, limit);
		return rSql;
	}

}
