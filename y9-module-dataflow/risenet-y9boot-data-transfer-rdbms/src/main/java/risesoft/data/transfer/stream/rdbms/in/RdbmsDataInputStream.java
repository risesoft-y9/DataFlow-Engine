package risesoft.data.transfer.stream.rdbms.in;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;


import risesoft.data.transfer.core.channel.InChannel;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.data.StringData;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.record.DefaultRecord;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.stream.in.DataInputStream;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;
import risesoft.data.transfer.stream.rdbms.utils.DBUtil;

/**
 * 数据库输入流
 * 
 * @typeName RdbmsDataInputStream
 * @date 2023年12月15日
 * @author lb
 */
public class RdbmsDataInputStream implements DataInputStream {

	private Connection connection;

	private String selectSql;

	private int fetchSize;

	private List<CreateColumnHandle> createColumnHandles;

	private String encoding;

	private Logger logger;

	public RdbmsDataInputStream(Connection connection, String selectSql, int fetchSize,
			List<CreateColumnHandle> createColumnHandles, String encoding, Logger logger) {
		super();
		this.connection = connection;
		this.selectSql = selectSql;
		this.fetchSize = fetchSize;
		this.createColumnHandles = createColumnHandles;
		this.encoding = encoding;
		this.logger = logger;
	}

	@Override
	public void close() throws Exception {
		try {
			connection.close();
		} catch (Exception e) {

		}

		logger.debug(this, "close input stream");
	}

	@Override
	public void read(Data data, InChannel inChannel) {
		ResultSet resultSet;
		String sql = selectSql + ((StringData) data).getValue();
		if (logger.isDebug()) {
			logger.debug(this, "work sql: " + sql);
		}
		try {
			resultSet = DBUtil.query(connection, sql, fetchSize);
		} catch (SQLException e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "sql 执行报错" + sql, e);
		}
		try {
			logger.debug(this, "readData:");
			while (resultSet.next()) {

				ResultSetMetaData metaData = resultSet.getMetaData();
				Record record = new DefaultRecord();
				try {
					for (int i = 0; i < createColumnHandles.size(); i++) {
						CreateColumnHandle createColumnHandle = createColumnHandles.get(i);
						record.addColumn(createColumnHandle.getColumn(resultSet, metaData, i + 1, encoding));
					}
					inChannel.writer(record);
				} catch (Exception e) {
					inChannel.collectDirtyRecord(record, e, "脏数据" + e.getMessage());
				}

			}
			inChannel.flush();
			logger.debug(this, "readData end");
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "读取数据报错", e);
		}finally {
			if (resultSet!=null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
