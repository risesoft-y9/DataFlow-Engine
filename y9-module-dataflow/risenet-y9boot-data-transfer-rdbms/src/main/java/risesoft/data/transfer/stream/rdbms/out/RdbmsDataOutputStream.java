package risesoft.data.transfer.stream.rdbms.out;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.record.Ack;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.stream.out.DataOutputStream;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

public abstract class RdbmsDataOutputStream implements DataOutputStream {
	protected Map<String, PreparedStatementHandle> createCloumnHandles;
	protected Triple<List<String>, List<Integer>, List<String>> resultSetMetaData;
	protected Connection connection;
	protected String workSql;
	protected DataBaseType dataBaseType;
	protected Logger logger;

	public RdbmsDataOutputStream(Connection connection, String workSql,
			Triple<List<String>, List<Integer>, List<String>> resultSetMetaData,
			Map<String, PreparedStatementHandle> createCloumnHandles, DataBaseType dataBaseType, Logger logger) {
		this.connection = connection;
		this.workSql = workSql;
		this.createCloumnHandles = createCloumnHandles;
		this.resultSetMetaData = resultSetMetaData;
		this.dataBaseType = dataBaseType;
		this.logger = logger;
	}

	@Override
	public void close() throws Exception {
		if (connection.isClosed()) {
			return;
		}
		connection.close();
		logger.debug(this, "close stream");
	}

	@Override
	public void writer(List<Record> records, Ack ack) {
		PreparedStatement preparedStatement = null;
		try {
			connection.setAutoCommit(false);
			logger.debug(this, " create Statement");
			preparedStatement = connection.prepareStatement(this.workSql);
			for (Record record : records) {
				preparedStatement = fillPreparedStatement(preparedStatement, record);
				preparedStatement.addBatch();
			}
			if (logger.isDebug()) {
				logger.debug(this, "executeBatch: " + records.size());
			}
			preparedStatement.executeBatch();
			connection.commit();
			ack.confirm(records);
			if (logger.isDebug()) {
				logger.debug(this, "confirm: " + records.size());
			}

		} catch (Exception e) {
			logger.error(this, e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "sql回滚失败", e1);
			}
			doOneWriter(records, ack);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 一条一条插入
	 * 
	 * @param records
	 * @param ack
	 */
	private void doOneWriter(List<Record> records, Ack ack) {
		PreparedStatement preparedStatement = null;
		try {
			connection.setAutoCommit(true);
			if (logger.isDebug()) {
				logger.debug(this, "doOneWriter" + records.size());
			}
			preparedStatement = connection.prepareStatement(this.workSql);
			for (Record record : records) {
				try {
					preparedStatement = fillPreparedStatement(preparedStatement, record);
					preparedStatement.execute();
					ack.confirm(record);
				} catch (Exception e) {
					logger.error(this, record + " error:" + e.getMessage());
					ack.cancel(record, e, e.toString());
				} finally {
					preparedStatement.clearParameters();
				}
			}
			if (logger.isDebug()) {
				logger.debug(this, "doOneWriter end" + records.size());
			}
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "执行插入报错", e);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

	}

	protected abstract PreparedStatement fillPreparedStatement(PreparedStatement preparedStatement, Record record)
			throws Exception;

	protected Map<String, Column> getRecordMap(Record record) {

		Map<String, Column> recordMap = new HashMap<String, Column>();
		for (int i = 0; i < record.getColumnNumber(); i++) {
			recordMap.put(record.getColumn(i).getName(), record.getColumn(i));

		}
		return recordMap;
	}

	@Override
	public void writer(Record record, Ack ack) {
		PreparedStatement preparedStatement = null;
		try {
			connection.setAutoCommit(true);
			preparedStatement = connection.prepareStatement(this.workSql);
			preparedStatement = fillPreparedStatement(preparedStatement, record);
			preparedStatement.execute();
			ack.confirm(record);
		} catch (Exception e) {
			e.printStackTrace();
			ack.cancel(record, e, e.getMessage());
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
