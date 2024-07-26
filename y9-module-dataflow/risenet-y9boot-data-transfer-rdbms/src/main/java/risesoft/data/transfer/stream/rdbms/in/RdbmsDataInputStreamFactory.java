package risesoft.data.transfer.stream.rdbms.in;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.data.StringData;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.FrameworkErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.stream.in.DataInputStream;
import risesoft.data.transfer.core.stream.in.DataInputStreamFactory;
import risesoft.data.transfer.core.util.ClassTools;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ValueUtils;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;
import risesoft.data.transfer.stream.rdbms.utils.DBUtil;
import risesoft.data.transfer.stream.rdbms.utils.DBUtilErrorCode;
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;
import risesoft.data.transfer.stream.rdbms.utils.RdbmsException;
import risesoft.data.transfer.stream.rdbms.utils.RdbmsRangeSplitWrap;

/**
 * 通用型数据库输入流工厂
 * 
 * @typeName RdbmsDataInputStreamFactory
 * @date 2023年12月14日
 * @author lb
 */
public class RdbmsDataInputStreamFactory implements DataInputStreamFactory {

	private static final List<CreateColumnHandle> CLOUMN_HANDLES;

	public static final byte[] EMPTY_CHAR_ARRAY = new byte[0];

	static {
		try {
			CLOUMN_HANDLES = ClassTools.getInstancesOfPack("risesoft.data.transfer.stream.rdbms.in.columns.impl",
					CreateColumnHandle.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("加载数据库处理工厂失败程序错误!");
		}

	}

	private String jdbcUrl;
	private String password;
	private String userName;
	private DataBaseType dataBaseType;
	private String selectSql;
	private String where;
	private String splitPk;
	private Boolean precise;
	private int splitFactor;
	private String tableName;
	private Connection connection;
	private List<CreateColumnHandle> createCloumnHandles;
	private int tableNumber;
	private int fetchSize;
	private double samplePercentage;

	private String mandatoryEncoding;

	private Logger logger;

	public RdbmsDataInputStreamFactory(Configuration configuration, LoggerFactory loggerFactory) {
		this.jdbcUrl = ValueUtils.getRequired(configuration.getString("jdbcUrl"), "缺失jdbcUrl");
		this.password = ValueUtils.getRequired(configuration.getString("password"), "缺失password");
		this.userName = ValueUtils.getRequired(configuration.getString("userName"), "缺失userName");
		dataBaseType = DataBaseType.RDBMS;
		this.tableName = ValueUtils.getRequired(configuration.getString("tableName"), "缺失tableName");
		this.selectSql = "select " + StringUtils
				.join(ValueUtils.getRequired(configuration.getList("column", String.class), "缺失column"), ",") + " from "
				+ this.tableName;
		this.where = configuration.getString("where", "").trim();
		// 其他地方有用到这个原始的where genPKSql pkRangeSQL = String.format("%s WHERE (%s AND %s IS
		// NOT NULL)", pkRangeSQL, where, splitPK);
//		if(StringUtils.isNotBlank(where) && !where.startsWith("where")) {
//			where = " where " + where;
//		}
		this.splitPk = configuration.getString("splitPk");
		this.precise = configuration.getBool("precise", false);
		this.splitFactor = configuration.getInt("splitFactor", -1);
		this.tableNumber = configuration.getInt("tableNumber", -1);
		this.fetchSize = configuration.getInt("fetchSize", 32);
		this.samplePercentage = configuration.getDouble("samplePercentage", 0.1);
		this.mandatoryEncoding = configuration.getString("mandatoryEncoding");
		logger = loggerFactory.getLogger(configuration.getString("name", "RdbmsDataInputStreamFactory"));
		if (logger.isInfo()) {
			logger.info(this, "create rdbmsDataInputStreamFactory jdbcUrl:" + jdbcUrl + "\n table:" + tableName
					+ " \n select sql: " + selectSql + "\n where" + where + "\n fetchSize: " + fetchSize);
		}
	}

	@Override
	public void init() {
		// 加载字段类型
		createCloumnHandles = new ArrayList<CreateColumnHandle>();
		try {
			logger.debug(this, "getConnection");
			this.connection = DBUtil.getConnection(DataBaseType.RDBMS, jdbcUrl, userName, password);
			if (logger.isDebug()) {
				logger.debug(this, "get metaData:" + this.selectSql + " where 1=2");
			}
			ResultSet resultSet = DBUtil.query(connection, this.selectSql + " where 1=2");
			ResultSetMetaData metaData = resultSet.getMetaData();
			int tmpType;
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				tmpType = metaData.getColumnType(i);
				for (CreateColumnHandle createCloumnHandle : CLOUMN_HANDLES) {
					if (createCloumnHandle.isHandle(tmpType)) {
						createCloumnHandles.add(createCloumnHandle);
						break;
					}
				}
				if (createCloumnHandles.size() != i) {
					throw TransferException.as(DBUtilErrorCode.UNSUPPORTED_TYPE, String.format(
							"您的配置文件中的列配置信息有误.  不支持数据库读取这种字段类型. 字段名:[%s], 字段名称:[%s], 字段Java类型:[%s]. 请尝试使用数据库函数将其转换支持的类型 或者不同步该字段 .",
							metaData.getColumnLabel(i), metaData.getColumnType(i), metaData.getColumnClassName(i)));
				}
			}
			logger.info(this, "初始化完成");
		} catch (Exception e) {
			throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "初始化数据库输入流工厂失败异常信息" + e.getMessage(), e);
		}

	}

	@Override
	public DataInputStream getStream() {
		return new RdbmsDataInputStream(DBUtil.getConnection(dataBaseType, jdbcUrl, userName, password), selectSql,
				fetchSize, createCloumnHandles, mandatoryEncoding, logger);
	}

	@Override
	public void close() throws Exception {
		if (this.connection.isClosed()) {
			return;
		}
		this.connection.close();

	}

	@Override
	public List<Data> splitToData(int executorSize) throws Exception {
		int numberSize = tableNumber != -1 ? tableNumber : this.splitFactor != -1 ? executorSize * splitFactor : 1;
		boolean isSub = numberSize >= 1 && StringUtils.isNotEmpty(this.splitPk);
		if (isSub) {
			if (logger.isInfo()) {
				logger.info(this, "sub data to" + numberSize);
			}
			List<Data> querys = null;
			ResultSet resultSet;
			ResultSetMetaData rsMetaData;
			if (precise) {
				resultSet = DBUtil.query(connection, "SELECT distinct " + splitPk + " from " + tableName);
				querys = new ArrayList<Data>();
				
				rsMetaData = resultSet.getMetaData();
				String fu = isStringType(rsMetaData.getColumnType(1)) ? "'%S'" : "%S";
				while (resultSet.next()) {
					querys.add(new StringData(String.format(" %S = " + fu, splitPk, resultSet.getObject(splitPk))));
				}
				resultSet.close();
			} else {
				String pk = genPKSql(splitPk, tableName, where);
				if (dataBaseType == DataBaseType.Oracle) {
					querys = StringData.as(genSplitSqlForOracle(splitPk, tableName, where, numberSize));
					return querys;
				}
				resultSet = DBUtil.query(connection, pk);
				rsMetaData = resultSet.getMetaData();
				Pair<Object, Object> minMaxPK = null;
				boolean isStringType = false;
				boolean isLongType = false;
				if (!isPKTypeValid(rsMetaData)) {
					throw new TransferException(CommonErrorCode.CONFIG_ERROR, "配置的splitPk不支持此类型");
				}
				if (isStringType(rsMetaData.getColumnType(1))) {
					while (DBUtil.asyncResultSetNext(resultSet)) {
						minMaxPK = new ImmutablePair<Object, Object>(resultSet.getString(1), resultSet.getString(2));
					}
					isStringType = true;
				} else if (isLongType(rsMetaData.getColumnType(1))) {
					isLongType = true;
					while (DBUtil.asyncResultSetNext(resultSet)) {
						minMaxPK = new ImmutablePair<Object, Object>(resultSet.getString(1), resultSet.getString(2));

						String minMax = resultSet.getString(1) + resultSet.getString(2);
						if (StringUtils.contains(minMax, '.')) {
							throw TransferException.as(DBUtilErrorCode.ILLEGAL_SPLIT_PK,
									"您配置的切分主键(splitPk)有误. 因为您配置的切分主键(splitPk) 类型不支持. 仅支持切分主键为一个,并且类型为整数或者字符串类型. 请尝试使用其他的切分主键或者联系 DBA 进行处理..pkSql:"
											+ pk + " type:" + rsMetaData.getColumnType(1));
						}
					}
				}
				resultSet.close();
				if (isStringType) {
					querys = StringData.as(RdbmsRangeSplitWrap.splitAndWrap(String.valueOf(minMaxPK.getLeft()),
							String.valueOf(minMaxPK.getRight()), numberSize, splitPk, "'", dataBaseType));
				} else if (isLongType) {
					querys = StringData
							.as(RdbmsRangeSplitWrap.splitAndWrap(new BigInteger(minMaxPK.getLeft().toString()),
									new BigInteger(minMaxPK.getRight().toString()), numberSize, splitPk));
				} else {
					throw TransferException.as(DBUtilErrorCode.ILLEGAL_SPLIT_PK,
							"您配置的切分主键(splitPk) 类型  不支持 仅支持切分主键为一个,并且类型为整数或者字符串类型. 请尝试使用其他的切分主键或者联系 DBA 进行处理.");
				}
			}
			// 需要加工where
			String whereStr = " where ";
			StringBuilder tmpWhereSB = new StringBuilder();
			if (StringUtils.isNotBlank(where)) {
				if (!where.toLowerCase().startsWith("where")) {
					tmpWhereSB.append(" where ").append(where);
				} else {
					tmpWhereSB.append(where);
				}
				whereStr = " and ";
			}
			String tmpWhere = tmpWhereSB.toString();
			StringData stringData;
			for (Data data : querys) {
				// where name='1' where name like 'aa' and name='1';
				stringData = (StringData) data;
				stringData.setValue(StringUtils.isEmpty(tmpWhere) ? (whereStr + stringData.getValue())
						: tmpWhere + whereStr + stringData.getValue());
//					stringData.setValue(whereStr + stringData.getValue());
			}
			if (logger.isInfo()) {
				logger.info(this, "sub data end: " + querys.size());
			}

			return querys;
		}
		this.close();
		logger.info(this, "no sub data");
		return Arrays.asList(new StringData(StringUtils.isEmpty(where) ? " " : (" where " + this.where)));
	}

	/**
	 * 检查类型
	 * 
	 * @param rsMetaData
	 * @return
	 */
	private boolean isPKTypeValid(ResultSetMetaData rsMetaData) {
		boolean ret = false;
		try {
			int minType = rsMetaData.getColumnType(1);
			int maxType = rsMetaData.getColumnType(2);
			boolean isStringType = isStringType(minType);
			boolean isNumberType = isLongType(minType);
			if (minType == maxType && (isNumberType || isStringType)) {
				ret = true;
			}
		} catch (Exception e) {
			throw TransferException.as(DBUtilErrorCode.ILLEGAL_SPLIT_PK,
					"获取切分主键(splitPk)字段类型失败. 该错误通常是系统底层异常导致. 请联系DBA处理.");
		}
		return ret;
	}

	private static String genPKSql(String splitPK, String table, String where) {

		String minMaxTemplate = "SELECT MIN(%s),MAX(%s) FROM %s";
		String pkRangeSQL = String.format(minMaxTemplate, splitPK, splitPK, table);
		if (StringUtils.isNotBlank(where)) {
			pkRangeSQL = String.format("%s WHERE (%s AND %s IS NOT NULL)", pkRangeSQL, where, splitPK);
		}
		return pkRangeSQL;
	}

	private List<String> genSplitSqlForOracle(String splitPK, String table, String where, int adviceNum) {
		if (adviceNum < 1) {
			throw new IllegalArgumentException(String.format("切分份数不能小于1. 此处:adviceNum=[%s].", adviceNum));
		} else if (adviceNum == 1) {
			return null;
		}
		String whereSql = String.format("%s IS NOT NULL", splitPK);
		if (StringUtils.isNotBlank(where)) {
			whereSql = String.format(" WHERE (%s) AND (%s) ", whereSql, where);
		} else {
			whereSql = String.format(" WHERE %s ", whereSql);
		}
		String sampleSqlTemplate = "SELECT * FROM ( SELECT %s FROM %s SAMPLE (%s) %s ORDER BY DBMS_RANDOM.VALUE) WHERE ROWNUM <= %s ORDER by %s ASC";
		String splitSql = String.format(sampleSqlTemplate, splitPK, table, samplePercentage, whereSql, adviceNum,
				splitPK);
		ResultSet rs = null;
		List<Pair<Object, Integer>> splitedRange = new ArrayList<Pair<Object, Integer>>();
		try {
			try {
				rs = DBUtil.query(connection, splitSql, fetchSize);
			} catch (Exception e) {
				throw RdbmsException.asQueryException(dataBaseType, e, splitSql, table, userName);
			}
			ResultSetMetaData rsMetaData = rs.getMetaData();
			while (DBUtil.asyncResultSetNext(rs)) {
				ImmutablePair<Object, Integer> eachPoint = new ImmutablePair<Object, Integer>(rs.getObject(1),
						rsMetaData.getColumnType(1));
				splitedRange.add(eachPoint);
			}
		} catch (TransferException e) {
			throw e;
		} catch (Exception e) {
			throw TransferException.as(DBUtilErrorCode.ILLEGAL_SPLIT_PK, "DataX尝试切分表发生错误. 请检查您的配置并作出修改.", e);
		}
		List<String> rangeSql = new ArrayList<String>();
		int splitedRangeSize = splitedRange.size();
		if (splitedRangeSize >= 2) {
			// warn: oracle Number is long type here
			if (isLongType(splitedRange.get(0).getRight())) {
				BigInteger[] integerPoints = new BigInteger[splitedRange.size()];
				for (int i = 0; i < splitedRangeSize; i++) {
					integerPoints[i] = new BigInteger(splitedRange.get(i).getLeft().toString());
				}
				rangeSql.addAll(RdbmsRangeSplitWrap.wrapRange(integerPoints, splitPK));
				// its ok if splitedRangeSize is 1
				rangeSql.add(RdbmsRangeSplitWrap.wrapFirstLastPoint(integerPoints[0],
						integerPoints[splitedRangeSize - 1], splitPK));
			} else if (isStringType(splitedRange.get(0).getRight())) {
				String[] stringPoints = new String[splitedRange.size()];
				for (int i = 0; i < splitedRangeSize; i++) {
					stringPoints[i] = new String(splitedRange.get(i).getLeft().toString());
				}
				rangeSql.addAll(RdbmsRangeSplitWrap.wrapRange(stringPoints, splitPK, "'", dataBaseType));
				rangeSql.add(RdbmsRangeSplitWrap.wrapFirstLastPoint(stringPoints[0], stringPoints[splitedRangeSize - 1],
						splitPK, "'", dataBaseType));
			} else {
				throw TransferException.as(DBUtilErrorCode.ILLEGAL_SPLIT_PK,
						"您配置的切分主键(splitPk)有误. 因为您配置的切分主键(splitPk) 类型 不支持.  仅支持切分主键为一个,并且类型为整数或者字符串类型. 请尝试使用其他的切分主键或者联系 DBA 进行处理.");
			}
		}
		return rangeSql;
	}

	private boolean isStringType(int type) {
		return type == Types.CHAR || type == Types.NCHAR || type == Types.VARCHAR || type == Types.LONGVARCHAR
				|| type == Types.NVARCHAR;
	}

	private boolean isLongType(int type) {
		boolean isValidLongType = type == Types.BIGINT || type == Types.INTEGER || type == Types.SMALLINT
				|| type == Types.TINYINT;
		switch (dataBaseType) {
		case Oracle:
			// TODO 手术刀:在数据库类型的时候没有判断是否为数字类型
		case RDBMS:
			isValidLongType |= type == Types.NUMERIC;
			// TODO 达梦要用DECIMAL
			isValidLongType |= type == Types.DECIMAL;
			break;
		default:
			break;
		}
		return isValidLongType;
	}
}
