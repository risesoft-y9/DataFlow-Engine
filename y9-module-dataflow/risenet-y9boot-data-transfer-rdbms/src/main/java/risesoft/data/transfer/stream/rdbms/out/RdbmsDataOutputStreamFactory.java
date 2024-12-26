package risesoft.data.transfer.stream.rdbms.out;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.FrameworkErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.stream.out.DataOutputStream;
import risesoft.data.transfer.core.stream.out.DataOutputStreamFactory;
import risesoft.data.transfer.core.util.ClassTools;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ValueUtils;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandle;
import risesoft.data.transfer.stream.rdbms.out.columns.PreparedStatementHandleFactory;
import risesoft.data.transfer.stream.rdbms.utils.DBUtil;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * 通用型数据库输出流工厂
 * 
 * @typeName RdbmsDataOutputStreamFactory
 * @date 2023年12月14日
 * @author lb
 */
public class RdbmsDataOutputStreamFactory implements DataOutputStreamFactory {

	private static final List<PreparedStatementHandleFactory> COLUMN_HANDLES;
	private static final Map<Integer, PreparedStatementHandle> PREPARED_MAP = new HashMap<Integer, PreparedStatementHandle>();
	public static final byte[] EMPTY_CHAR_ARRAY = new byte[0];

	static {
		try {
			COLUMN_HANDLES = ClassTools.getInstancesOfPack("risesoft.data.transfer.stream.rdbms.out.columns.impl",
					PreparedStatementHandleFactory.class);
			Field[] fields = Types.class.getFields();
			Object value;
			PreparedStatementHandle preparedStatementHandle;
			for (Field field : fields) {
				value = field.get(null);
				preparedStatementHandle = createHandle((int) value);
				if (preparedStatementHandle != null) {
					PREPARED_MAP.put((int) value, preparedStatementHandle);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("加载数据库处理工厂失败程序错误!");
		}

	}

	protected String jdbcUrl;
	protected String password;
	protected String userName;
	protected DataBaseType dataBaseType;
	protected String tableName;
	protected String writerType;
	protected Map<String, PreparedStatementHandle> createColumnHandles;
	protected Triple<List<String>, List<Integer>, List<String>> resultSetMetaData;
	protected List<String> columns;
	protected String workSql;
	protected Logger logger;

	public RdbmsDataOutputStreamFactory(Configuration configuration, JobContext jobContext) {
		this.jdbcUrl = ValueUtils.getRequired(configuration.getString("jdbcUrl"), "缺失jdbcUrl");
		this.password = ValueUtils.getRequired(configuration.getString("password"), "缺失password");
		this.userName = ValueUtils.getRequired(configuration.getString("userName"), "缺失userName");
		this.dataBaseType = DataBaseType.getDataBaseTypeByJdbcUrl(this.jdbcUrl);
		this.tableName = ValueUtils.getRequired(configuration.getString("tableName"), "缺失tableName");
		this.writerType = configuration.getString("writerType", "insert");
		this.columns = ValueUtils.getRequired(configuration.getList("column", String.class), "缺失列配置");
		this.logger = jobContext.getLoggerFactory().getLogger(RdbmsDataOutputStreamFactory.class);
		if (logger.isInfo()) {
			logger.info(this, "create RdbmsDataOutputStreamFactory \njdbcUrl:" + jdbcUrl + " \ntableName:" + tableName
					+ "\ncolumns:" + this.columns + "\nwriterType:" + writerType);
		}
	}

	protected List<String> idField;
	protected List<String> updateField;

	/**
	 * 创建insertSql
	 * 
	 * @param size
	 */
	protected void createInsertSql(int size) {

		StringBuilder sb = new StringBuilder("insert into ").append(tableName).append(" (")
				.append(StringUtils.join(this.resultSetMetaData.getLeft(), ",")).append(") values (");
		for (int i = 0; i < size; i++) {
			sb.append("?");
			if (i != size - 1) {
				sb.append(",");
			}
		}
		sb.append(")");

		this.workSql = sb.toString();
		if (logger.isInfo()) {
			logger.info(this, "worksql:" + this.workSql);
		}
	}

	/**
	 * 创建replace
	 * 
	 * @param size
	 */
	protected void createReplace(int size) {
		idField = Arrays.asList(getStrings(writerType, "replace"));
		updateField = new ArrayList<String>();
		for (String columnHolder : resultSetMetaData.getLeft()) {
			if (!idField.contains(columnHolder)) {
				updateField.add(columnHolder);
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("MERGE INTO " + tableName + " A USING ( SELECT ");
		boolean first = true;
		boolean first1 = true;
		StringBuilder str = new StringBuilder();
		StringBuilder update = new StringBuilder();
		for (String columnHolder : idField) {
			if (!first) {
				sb.append(",");
				str.append(" AND ");
			} else {
				first = false;
			}
			str.append("TMP.").append(columnHolder);
			sb.append("?");
			str.append(" = ");
			sb.append(" AS ");
			str.append("A.").append(columnHolder);
			sb.append(columnHolder);

		}

		for (String columnHolder : updateField) {
			if (!first1) {
				update.append(",");
			} else {
				first1 = false;
			}
			update.append(columnHolder);
			update.append(" = ");
			update.append("?");

		}

		sb.append(" FROM DUAL ) TMP ON (");
		sb.append(str);
		sb.append(" ) WHEN MATCHED THEN UPDATE SET ");
		sb.append(update);
		sb.append(" WHEN NOT MATCHED THEN ").append("INSERT (")
				.append(StringUtils.join(resultSetMetaData.getLeft(), ",")).append(") VALUES(");
		for (int i = 0; i < size; i++) {
			sb.append("?");
			if (i != size - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		this.workSql = sb.toString();
		if (logger.isInfo()) {
			logger.info(this, "worksql:" + this.workSql);
		}
	}

	/**
	 * 创建updateWorkSql
	 * 
	 * @param size
	 */
	protected void createUpdate(int size) {
		idField = Arrays.asList(getStrings(writerType, "update"));
		updateField = new ArrayList<String>();
		for (String columnHolder : resultSetMetaData.getLeft()) {
			if (!idField.contains(columnHolder)) {
				updateField.add(columnHolder);
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("MERGE INTO " + tableName + " A USING ( SELECT ");
		boolean first = true;
		boolean first1 = true;
		StringBuilder str = new StringBuilder();
		StringBuilder update = new StringBuilder();
		for (String columnHolder : idField) {
			if (!first) {
				sb.append(",");
				str.append(" AND ");
			} else {
				first = false;
			}
			str.append("TMP.").append(columnHolder);
			sb.append("?");
			str.append(" = ");
			sb.append(" AS ");
			str.append("A.").append(columnHolder);
			sb.append(columnHolder);

		}

		for (String columnHolder : updateField) {
			if (!first1) {
				update.append(",");
			} else {
				first1 = false;
			}
			update.append(columnHolder);
			update.append(" = ");
			update.append("?");

		}

		sb.append(" FROM DUAL ) TMP ON (");
		sb.append(str);
		sb.append(" ) WHEN MATCHED THEN UPDATE SET ");
		sb.append(update);
		sb.append(" where ");
		first = true;
		// 拼接or 有一个改变都得update
		PreparedStatementHandle preparedStatementHandle;
		String nvl = getNvlFunction();
		for (String columnHolder : updateField) {
			if (!first) {
				sb.append(" or ");
			} else {
				first = false;
			}
			// 如果是大文本则需要为length
			preparedStatementHandle = createColumnHandles.get(columnHolder);
			if (preparedStatementHandle.isBigType()) {
				sb.append("length(" + nvl + "(" + columnHolder + "," + preparedStatementHandle.nullValue() + ")"
						+ ") != length(" + nvl + "(?," + preparedStatementHandle.nullValue() + "))");
			} else {
				sb.append(nvl + "(" + columnHolder + "," + preparedStatementHandle.nullValue() + ")" + " != " + nvl
						+ "(?," + preparedStatementHandle.nullValue() + ")");
			}
		}
		sb.append(" WHEN NOT MATCHED THEN ").append("INSERT (")
				.append(StringUtils.join(resultSetMetaData.getLeft(), ",")).append(") VALUES(");
		for (int i = 0; i < size; i++) {
			sb.append("?");
			if (i != size - 1) {
				sb.append(",");
			}
		}
		sb.append(")");
		this.workSql = sb.toString();
		if (logger.isInfo()) {
			logger.info(this, "worksql:" + this.workSql);
		}
	}

	/**
	 * 获取当前数据库的空值判断函数
	 * 
	 * @return
	 */
	protected String getNvlFunction() {
		switch (dataBaseType) {
		case Oracle:
		case DM:
			return "NVL";
		default:
			return "COALESCE";
		}
	}

	@Override
	public void init() {
		// 加载字段类型
		createColumnHandles = new HashMap<String, PreparedStatementHandle>();
		try {
			logger.debug(this, "getMetaData");
			this.resultSetMetaData = DBUtil.getColumnMetaData(dataBaseType, jdbcUrl, userName, password, tableName,
					StringUtils.join(this.columns, ","));
			// 构建一个map
			int size = this.resultSetMetaData.getRight().size();
			for (int i = 0; i < size; i++) {
				createColumnHandles.put(this.resultSetMetaData.getLeft().get(i),
						getHandle(this.resultSetMetaData.getMiddle().get(i)));
			}
			// 构建sql
			logger.debug(this, "create writer sql");
			if (writerType.equals("insert")) {
				createInsertSql(size);
			} else if (writerType.startsWith("update")) {
				createUpdate(size);
			} else if (writerType.startsWith("replace")) {
				createReplace(size);
			} else {
				logger.error(this, "未识别的输出类型" + writerType);
				throw new RuntimeException("未识别的输出类型" + writerType);
			}
		} catch (Exception e) {
			throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "初始化数据库输入流工厂失败异常信息" + e.getMessage(), e);
		}

	}

	public static String[] getStrings(String merge, String pre) {
		merge = merge.replace(pre, "");
		merge = merge.replace("(", "");
		merge = merge.replace(")", "");
		merge = merge.replace(" ", "");
		return merge.split(",");
	}

	/**
	 * 获取对应类型的处理器
	 * 
	 * @param type
	 * @return
	 */
	private static PreparedStatementHandle createHandle(int type) {
		for (PreparedStatementHandleFactory preparedStatementHandleFactory : COLUMN_HANDLES) {
			if (preparedStatementHandleFactory.isHandle(type)) {
				return preparedStatementHandleFactory.getPreparedStatementHandle(type);
			}
		}
		return null;
	}

	private static PreparedStatementHandle getHandle(int type) {
		PreparedStatementHandle preparedStatementHandle = PREPARED_MAP.get(type);
		if (preparedStatementHandle == null) {
			throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "无法处理的数据库类型:" + type);
		}
		return preparedStatementHandle;
	}

	/**
	 * 关闭使用的链接
	 */
	@Override
	public void close() throws Exception {
		logger.info(this, "close");
	}

	protected DataOutputStream getInsertStream() {
		return new InsertRdbmsDataOutputStream(DBUtil.getConnection(dataBaseType, jdbcUrl, userName, password), workSql,
				resultSetMetaData, createColumnHandles, dataBaseType, logger);
	}

	protected DataOutputStream getUpdateStream() {
		return new UpdateRdbmsDataOutputStream(DBUtil.getConnection(dataBaseType, jdbcUrl, userName, password), workSql,
				resultSetMetaData, createColumnHandles, dataBaseType, idField, updateField, logger);
	}

	protected DataOutputStream getReplaceStream() {
		return new ReplaceRdbmsDataOutputStream(DBUtil.getConnection(dataBaseType, jdbcUrl, userName, password),
				workSql, resultSetMetaData, createColumnHandles, dataBaseType, idField, updateField, logger);
	}

	@Override
	public DataOutputStream getStream() {
		logger.debug(this, "getstream");
		if (writerType.equals("insert")) {
			return getInsertStream();
		}
		if (writerType.startsWith("update")) {
			return getUpdateStream();
		}
		if (writerType.startsWith("replace")) {
			return getReplaceStream();
		}
		throw TransferException.as(CommonErrorCode.CONFIG_ERROR, "无效的输出类型" + writerType);
	}
}
