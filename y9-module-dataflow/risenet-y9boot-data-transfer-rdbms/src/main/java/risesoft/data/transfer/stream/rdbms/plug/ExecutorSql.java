package risesoft.data.transfer.stream.rdbms.plug;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.stream.rdbms.utils.DBUtil;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * 执行sql插件
 * 
 * 
 * @typeName ExecutorSql
 * @date 2024年12月30日
 * @author lb
 */
public class ExecutorSql {

	/**
	 * sql语句
	 */
	protected String sql;
	/**
	 * 数据库url
	 */
	protected String jdbcUrl;
	/**
	 * 用户名
	 */
	protected String jdbcUserName;
	/**
	 * 密码
	 */
	protected String jdbcPassword;

	public ExecutorSql() {
		super();
	}

	public ExecutorSql(String sql, String jdbcUrl, String jdbcUserName, String jdbcPassword) {
		super();
		this.sql = sql;
		this.jdbcUrl = jdbcUrl;
		this.jdbcUserName = jdbcUserName;
		this.jdbcPassword = jdbcPassword;
	}

	public Object doSql() {
		Connection connection = DBUtil.getConnection(DataBaseType.RDBMS, jdbcUrl, jdbcUserName, jdbcPassword);
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			if (sql.startsWith("select")) {
				resultSet = statement.executeQuery(sql);
				return DBUtil.getResultSetAsList(resultSet);
			} else {
				return statement.executeUpdate(sql);
			}
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "执行sql语句出错!" + e.getMessage() + ",相关sql:" + sql,
					e);
		} finally {
			DBUtil.closeResultSet(resultSet);
			DBUtil.closeDBResources(statement, connection);
		}
	}

}
