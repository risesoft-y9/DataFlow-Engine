package risesoft.data.transfer.stream.rdbms.instruction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.instruction.factory.Instruction;
import risesoft.data.transfer.stream.rdbms.utils.DBUtil;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * 方法指令
 * 
 * 
 * @typeName SQLFunctionInstruction
 * @date 2024年8月27日
 * @author lb
 */
public class SQLFunctionInstruction implements Instruction {
	/**
	 * 待执行的sql
	 */
	private String sql;
	/**
	 * 数据库url
	 */
	private String jdbcUrl;
	/**
	 * 用户名
	 */
	private String jdbcUserName;
	/**
	 * 密码
	 */
	private String jdbcPassword;

	public SQLFunctionInstruction(String sql, String jdbcUrl, String jdbcUserName, String jdbcPassword) {
		super();
		this.sql = sql;
		this.jdbcUrl = jdbcUrl;
		this.jdbcUserName = jdbcUserName;
		this.jdbcPassword = jdbcPassword;
	}

	@Override
	public String executor(String config, JobContext jobContext) {
		Connection connection = DBUtil.getConnection(DataBaseType.RDBMS, jdbcUrl, jdbcUserName, jdbcPassword);
		ResultSet resultSet = null;
		try {

			resultSet = DBUtil.query(connection, sql);

			return resultSet.next() ? resultSet.getObject(1) + "" : "";
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "执行sql语句出错!" + e.getMessage() + ",相关sql:" + sql,
					e);
		} finally {
			DBUtil.closeResultSet(resultSet);
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}

}
