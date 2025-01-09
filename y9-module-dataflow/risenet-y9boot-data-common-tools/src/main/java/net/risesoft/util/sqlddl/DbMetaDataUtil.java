package net.risesoft.util.sqlddl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbMetaDataUtil {

	private static Logger log = LoggerFactory.getLogger(DbMetaDataUtil.class);

	/**
     * 判断数据库连接状态
     */
    public static Boolean getConnection(String driverClass, String userName, String passWord, String url) {
    	boolean flag = false;
    	Connection connection = null;
        try {
            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, userName, passWord);
            flag = true;
        } catch (ClassNotFoundException e) {
			log.error(driverClass + "-驱动包不存在");
		} catch (SQLException e) {
			e.printStackTrace();
			log.error(url + "-连接失败");
		} finally {
			ReleaseResource(connection, null, null, null);
		}
        return flag;
    }

    /**
     * 释放资源
     */
    public static void ReleaseResource(Connection connection, PreparedStatement pstm, ResultSet rs, Statement stmt) {
    	if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pstm != null) {
            try {
                pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
            	stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查表是否存在
     * @param dataSource
     * @param tableName
     * @return
     * @throws Exception
     */
	public static boolean checkTableExist(DataSource dataSource, String tableName) throws Exception {
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			String dialect = dbmd.getDatabaseProductName().toLowerCase();
			if ("mysql".equals(dialect) || "microsoft".equals(dialect)) {
				rs = dbmd.getTables(connection.getCatalog(), null, tableName, new String[] { "TABLE", "VIEW" });
			} else {
				//获取模式名
//				String schema = dbmd.getUserName().toUpperCase();
				String schema = connection.getSchema();
				rs = dbmd.getTables(null, schema, tableName, new String[] { "TABLE", "VIEW" });
			}
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			ReleaseResource(connection, null, rs, null);
		}
		return false;
	}
	
	/**
     * 获取数据库所有表
     * @param driverClass
     * @param userName
     * @param passWord
     * @param url
     * @return
     */
    public static List<String> getAllTable(String driverClass, String userName, String passWord, String url, String schema){
    	List<String> list = new ArrayList<String>();
    	Connection connection = null;
    	ResultSet tables = null;
		try {
			// 加载驱动
			Class.forName(driverClass);
			// 获得数据库连接
			connection = DriverManager.getConnection(url, userName, passWord);
			// 获得元数据
			DatabaseMetaData metaData = connection.getMetaData();
			// 获得表信息
			schema = StringUtils.isBlank(schema)?null:schema;
			String dialect = metaData.getDatabaseProductName().toLowerCase();
			if ("mysql".equals(dialect) || "microsoft".equals(dialect)) {
				tables = metaData.getTables(connection.getCatalog(), null, null, new String[] { "TABLE", "VIEW" });
			} else {
				tables = metaData.getTables(null, schema, null, new String[] { "TABLE", "VIEW" });
			}
			while (tables.next()) {
			    // 获得表名
			    String table_name = tables.getString("TABLE_NAME");
			    // 获取表所属schema
			    String table_schema = tables.getString("TABLE_SCHEM");
			    if(StringUtils.isNotBlank(table_schema) && !table_schema.equals(connection.getSchema())) {
			    	table_name = table_schema + "." + table_name;
			    }
			    list.add(table_name);
			}
		} catch (ClassNotFoundException e) {
			log.error(driverClass + "-驱动包不存在");
		} catch (SQLException e) {
			log.error(url + "-连接失败：" + e.getMessage());
		} finally {
			ReleaseResource(connection, null, tables, null);
		}
		return list;
    }

	/**
	 * 获取表字段信息
	 * @param dataSource
	 * @param tableName
	 * @param columnNamePatten
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "resource" })
	public static List<DbColumn> listAllColumns(DataSource dataSource, String tableName, String columnNamePatten) throws Exception {
		Connection connection = null;
		String table_schema = null;
		String databaseName = null;

		Statement stmt = null;
		ResultSet rs = null;
		List<DbColumn> dbColumnList = new ArrayList<DbColumn>();
		if (DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
			try {
				connection = dataSource.getConnection();
				try {
					databaseName = connection.getCatalog();
				} catch (Exception e) {
				}

				DatabaseMetaData dbmd = connection.getMetaData();
				table_schema = dbmd.getUserName().toUpperCase();
				String dialect = getDatabaseDialectName(dataSource);

				// 获得主键
				if ("mysql".equals(dialect)) {
					rs = dbmd.getPrimaryKeys(null, databaseName, tableName);
				} else if ("mssql".equals(dialect)) {
					rs = dbmd.getPrimaryKeys(databaseName, null, tableName);
				} else if ("oracle".equals(dialect)) {
					rs = dbmd.getPrimaryKeys(null, table_schema, tableName);
				} else if ("dm".equals(dialect)) {
					rs = dbmd.getPrimaryKeys(null, table_schema, tableName);
				}else {
					rs = dbmd.getPrimaryKeys(null, connection.getSchema(), tableName);
				}
				List<String> pkList = new ArrayList<String>();
				while (rs.next()) {
					pkList.add(rs.getString("COLUMN_NAME"));
				}

				if (pkList.size() == 0) {
					// throw new Exception("***********没有主键？*************");
				}

				// 获得所有列的属性
				if ("mysql".equals(dialect)) {
					rs = dbmd.getColumns(null, databaseName, tableName, columnNamePatten);
				} else if ("mssql".equals(dialect)) {
					rs = dbmd.getColumns(databaseName, null, tableName, columnNamePatten);
				} else if ("oracle".equals(dialect)) {
					rs = dbmd.getColumns(null, table_schema, tableName, columnNamePatten);
				}else if ("dm".equals(dialect)) {
					rs = dbmd.getColumns(null, table_schema, tableName, columnNamePatten);
				}else {
					rs = dbmd.getColumns(null, connection.getSchema(), tableName, columnNamePatten);
				}

				while (rs.next()) {
					DbColumn dbColumn = new DbColumn();

					dbColumn.setTable_name((String) rs.getString("table_name").toUpperCase());

					String columnName = (String) rs.getString("column_name".toLowerCase());
					dbColumn.setColumn_name(columnName);
					dbColumn.setColumn_name_old(columnName);

					if (pkList.contains(columnName)) {
						dbColumn.setPrimaryKey(true);
					} else {
						dbColumn.setPrimaryKey(false);
					}

					String remarks = (String) rs.getString("remarks");
					if (StringUtils.isNotBlank(remarks)) {
						dbColumn.setComment(remarks);
					}
					dbColumn.setData_length(rs.getString("column_size"));

					int data_type = rs.getInt("data_type");
					dbColumn.setData_type(data_type);
					dbColumn.setType_name(((String) rs.getString("type_name")).toLowerCase());

					String nullable = (String) rs.getString("is_nullable");
					Boolean bNullable = false;
					if ("yes".equalsIgnoreCase(nullable)) {
						bNullable = true;
					}
					dbColumn.setNullable(bNullable);

					boolean exist = false;
					for (DbColumn field : dbColumnList) {
						if (field.getColumn_name().equalsIgnoreCase(columnName)) {
							exist = true;
							break;
						}
					}
					if (!exist) {
						dbColumnList.add(dbColumn);
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				throw e;
			} finally {
				ReleaseResource(connection, null, rs, stmt);
			}
		}
		return dbColumnList;
	}

	/**
	 * 获取数据库版本
	 * @param dataSource
	 * @return
	 * @throws SQLException
	 */
	public static String getDatabaseProductVersion(DataSource dataSource) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			return dbmd.getDatabaseProductVersion();
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			connection.close();
		}
	}

	/**
	 * 获取数据库名称
	 * @param dataSource
	 * @return
	 */
	public static String getDatabaseDialectName(DataSource dataSource) {
		String databaseName = "";
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			databaseName = dbmd.getDatabaseProductName().toLowerCase();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (databaseName.indexOf("microsoft") > -1) {
			return "mssql";
		}else if(databaseName.indexOf("dbms") > -1) {
			return "dm";
		}else if(databaseName.indexOf("kingbasees") > -1) {
			return "kingbase";
		}
		return databaseName;
	}
	
	/**
	 * 执行DDL语句
	 * @param dataSource
	 * @param DDL
	 * @return
	 * @throws SQLException
	 */
	public static Boolean executeDDL(DataSource dataSource, String DDL) throws SQLException {
		Connection connection = null;
		Statement stmt = null;
		Boolean bool = false;
		try {
			connection = dataSource.getConnection();
			stmt = connection.createStatement();
			System.out.println(DDL);
			bool = stmt.execute(DDL);
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			stmt.close();
			connection.close();
		}
		return bool;
	}
	
	/**
	 * 获取数据库表、字段、数据量
	 * @param driverClass
	 * @param userName
	 * @param passWord
	 * @param url
	 * @param schema
	 * @return
	 */
	public static Map<String, Object> getDataNum(String driverClass, String userName, String passWord, String url,String schema){
    	Connection connection = null;
    	ResultSet tables = null;
    	ResultSet columns = null;
    	// 创建预编译语句对象
	    PreparedStatement pstm = null;
	    ResultSet rs = null;
	    Map<String, Object> map = new HashMap<String, Object>();
		try {
			//加载驱动
			Class.forName(driverClass);
			//获得数据库连接
			connection = DriverManager.getConnection(url, userName, passWord);
			//获得元数据
			DatabaseMetaData metaData = connection.getMetaData();
			//获得表信息
			schema = StringUtils.isBlank(schema)?null:schema.toUpperCase();
			tables = metaData.getTables(null, schema, null, new String[]{"TABLE"});
			int tableNum = 0,fieldNum = 0,dataNum = 0;
			while (tables.next()) {
				tableNum ++;
			    //获得表名
			    String table_name = tables.getString("TABLE_NAME");
			    String sql = "select count(*) as result from "+table_name;
			    pstm = connection.prepareStatement(sql);
	            rs = pstm.executeQuery();
				while (rs.next()) {
					dataNum += rs.getInt(1);
				}
			    //通过表名获得所有字段名
			    columns = metaData.getColumns(null, schema, table_name, "%");
			    while (columns.next()) {
			    	fieldNum ++;
			    }
			    ReleaseResource(null, pstm, rs, null);
			    ReleaseResource(null, null, columns, null);
			}
			map.put("tableNum", tableNum);
			map.put("fieldNum", fieldNum);
			map.put("dataNum", dataNum);
		} catch (ClassNotFoundException e) {
			log.error(driverClass + "-驱动包不存在");
		} catch (SQLException e) {
			log.error(url + "-连接失败");
		} finally {
			ReleaseResource(connection, pstm, tables, null);
			ReleaseResource(null, null, columns, null);
			ReleaseResource(null, null, rs, null);
		}
		return map;
    }
	
	/**
	 * 获取表数据量
	 * @param driverClass
	 * @param userName
	 * @param passWord
	 * @param url
	 * @param tableName
	 * @return
	 */
    public static long getTableDataNum(DataSource dataSource, String tableName) {
    	Connection connection = null;
    	Statement stmt = null;
    	long count = 0;
		try {
			//获得数据库连接
			connection = dataSource.getConnection();
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
			if (rs.next()) {
			    count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ReleaseResource(connection, null, null, stmt);
		}
    	return count;
    }

}
