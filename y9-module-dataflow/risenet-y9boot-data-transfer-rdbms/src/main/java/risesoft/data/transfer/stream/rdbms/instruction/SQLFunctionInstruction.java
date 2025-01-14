package risesoft.data.transfer.stream.rdbms.instruction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import com.alibaba.fastjson2.util.DateUtils;

import oracle.sql.TIMESTAMP;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.instruction.factory.Instruction;
import risesoft.data.transfer.stream.rdbms.utils.DBUtil;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * 方法指令 执行数据库语句传入数据库语句执行方法
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

	private DataBaseType dataBaseType;

	public SQLFunctionInstruction(String sql, String jdbcUrl, String jdbcUserName, String jdbcPassword,
			DataBaseType dataBase) {
		super();
		this.sql = sql;
		this.jdbcUrl = jdbcUrl;
		this.dataBaseType = dataBase;
		this.jdbcUserName = jdbcUserName;
		this.jdbcPassword = jdbcPassword;
	}

	@Override
	public String executor(String config, JobContext jobContext) {
		Connection connection = DBUtil.getConnection(DataBaseType.RDBMS, jdbcUrl, jdbcUserName, jdbcPassword);
		ResultSet resultSet = null;
		try {
			resultSet = DBUtil.query(connection, sql);
			if (!resultSet.next()) {
				return "";
			}
			return getData(resultSet);
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

	public String getData(ResultSet rs) throws SQLException {
		int type = rs.getMetaData().getColumnType(1);
		String toDate = dataBaseType == DataBaseType.MySql ? " str_to_date" : " to_date";
		String format = dataBaseType == DataBaseType.MySql ? "%Y-%m-%d %H:%i:%s" : "yyyy-mm-dd hh24:mi:ss";
		if (dataBaseType == DataBaseType.SQLServer) {
			return " CAST('" + rs.getString(1) + "' AS DATE)";
		}
		switch (type) {
		case Types.CHAR:
		case Types.NCHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
		case Types.NVARCHAR:
		case Types.LONGNVARCHAR:
			return "'" + rs.getString(1) + "'";
		case Types.SMALLINT:
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.TINYINT:
			return rs.getBigDecimal(1) + "";
		case Types.NUMERIC:
		case Types.DECIMAL:
		case Types.FLOAT:
		case Types.REAL:
		case Types.DOUBLE:
			return rs.getBigDecimal(1) + "";
		case Types.DATE:
			java.sql.Date sqlDate = rs.getDate(1);
			return toDate + "('" + DateUtils.format(sqlDate, "yyyy-MM-dd HH:mm:ss") + "','" + format + "')";
		case Types.TIME:
			java.sql.Time sqlTime = rs.getTime(1);
			return toDate + "('" + DateUtils.format(sqlTime, "yyyy-MM-dd HH:mm:ss") + "','" + format + "')";
		case Types.TIMESTAMP:
		case -101:
			java.sql.Timestamp sqlTimestamp = rs.getTimestamp(1);
			return toDate + "('" + DateUtils.format(sqlTimestamp, "yyyy-MM-dd HH:mm:ss") + "','" + format + "')";
		case Types.BOOLEAN:
			return rs.getBoolean(1) + "";
		default:
			throw new RuntimeException("未识别或不支持的类型" + type);
		}
	}

}
