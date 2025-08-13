package risesoft.data.transfer.stream.rdbms.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import risesoft.data.transfer.core.exception.TransferException;

/**
 * refer:http://blog.csdn.net/ring0hx/article/details/6152528
 * <p/>
 */
public enum DataBaseType {
	MySql("mysql", "com.mysql.jdbc.Driver"), Tddl("mysql", "com.mysql.jdbc.Driver"),
	DRDS("drds", "com.mysql.jdbc.Driver"), Oracle("oracle", "oracle.jdbc.OracleDriver"),
	SQLServer("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
	PostgreSQL("postgresql", "org.postgresql.Driver"),
	RDBMS("rdbms", "risesoft.data.transfer.stream.rdbms.utils.DataBaseType"), DB2("db2", "com.ibm.db2.jcc.DB2Driver"),
	ADS("ads", "com.mysql.jdbc.Driver"), ClickHouse("clickhouse", "ru.yandex.clickhouse.ClickHouseDriver"),
	KingbaseES("kingbasees", "com.kingbase8.Driver"), Oscar("oscar", "com.oscar.Driver"),
	DM("dm", "dm.jdbc.driver.DmDriver");

	private String typeName;
	private String driverClassName;
	// 数据库值key
	private static final Map<DataBaseType, Set<String>> DATABASE_KEY = new HashMap<DataBaseType, Set<String>>();

	static {
		DATABASE_KEY.put(DataBaseType.MySql, new HashSet<String>(
				Arrays.asList("SELECT、INSERT、UPDATE、DELETE、WHERE、GROUP、ORDER、BY、FROM、TABLE、AND、OR、NOT、NULL、"
						+ "AS、DISTINCT、JOIN、ON、IN、INTO、VALUES、LIKE、IS、BETWEEN、INNER、LEFT、RIGHT、"
						+ "FULL、OUTER、CROSS、NATURAL、UNION、ALL、ANY、SOME、EXISTS、"
						+ "UNIQUE、PRIMARY、KEY、FOREIGN、REFERENCES、CASCADE、SET、CHECK、"
						+ "CONSTRAINT、DEFAULT、AUTO_INCREMENT、COLLATE、CHARACTER、VARCHAR、INT、FLOAT、DATE、TIME、"
						+ "DATETIME、TIMESTAMP、YEAR、CHAR、TEXT、BLOB、ENUM、SERIALIZABLE、REPEATABLE、READ、COMMITTED、"
						+ "UNCOMMITTED、PHANTOM、READS、WRITE、SHARED、EXCLUSIVE、LOCK、UNLOCK、OPTIMISTIC、PESSIMISTIC、WITH、GRANT、REVOKE"
								.split("、"))));
		DATABASE_KEY.put(DataBaseType.Oracle, new HashSet<String>(Arrays.asList(
				"ACCESS、ADD、ALL、ALTER、AND、ANY、AS、ASC、AUDIT、BETWEEN、BY、CHAR、CHECK、CLUSTER、COLUMN、COMMENT、COMPRESS、CONNECT、CREATE、CURRENT、DATE、DECIMAL、DEFAULT、DELETE、DESC、DISTINCT、DROP、ELSE、EXCLUSIVE、EXISTS、FILE、FLOAT、FOR、FROM、GRANT、GROUP、HAVING、IDENTIFIED、IMMEDIATE、IN、INCREMENT、INDEX、INITIAL、INSERT、INTEGER、INTERSECT、INTO、IS、LEVEL、LIKE、LOCK、LONG、MAXEXTENTS、MINUS、MLSLABEL、MOD、MODE、MODIFY、NOAUDIT、NOCOMPRESS、NOT、NOWAIT、NULL、NUMBER、OF、OFFLINE、ON、ONLINE、OPTION、OR、ORDER、PCTFREE、PRIOR、PRIVILEGES、PUBLIC、RAW、RENAME、RESOURCE、REVOKE、ROW、ROWID、ROWNUM、ROWS、SELECT、SESSION、SET、SHARE、SIZE、SMALLINT、START、SUCCESSFUL、SYNONYM、SYSDATE、TABLE、THEN、TO、TRIGGER、UID、UNION、UNIQUE、UPDATE、USER、VALIDATE、VALUES、VARCHAR、VARCHAR2、VIEW、WHENEVER、WHERE、WITH"
						.split("、"))));
		DATABASE_KEY.put(DataBaseType.DM, new HashSet<String>(Arrays.asList(
				"ACCESS、ADD、ADMIN、ALL、ALTER、AND、ANY、AS、ASC、AUDIT、BETWEEN、BY、CHAR、CHECK、CLUSTER、COLUMN、COMMENT、COMPRESS、CONNECT、CREATE、CURRENT、DATE、DECIMAL、DEFAULT、DELETE、DESC、DISTINCT、DROP、ELSE、EXCLUSIVE、EXISTS、FILE、FLOAT、FOR、FROM、GRANT、GROUP、HAVING、IDENTIFIED、IMMEDIATE、IN、INCREMENT、INDEX、INITIAL、INSERT、INTEGER、INTERSECT、INTO、IS、LEVEL、LIKE、LOCK、LONG、MAXEXTENTS、MINUS、MLSLABEL、MOD、MODE、MODIFY、NOAUDIT、NOCOMPRESS、NOT、NOWAIT、NULL、NUMBER、OF、OFFLINE、ON、ONLINE、OPTION、OR、ORDER、PCTFREE、PRIOR、PRIVILEGES、PUBLIC、RAW、RENAME、RESOURCE、REVOKE、ROW、ROWID、ROWNUM、ROWS、SELECT、SESSION、SET、SHARE、SIZE、SMALLINT、START、SUCCESSFUL、SYNONYM、SYSDATE、TABLE、THEN、TO、TRIGGER、UID、UNION、UNIQUE、UPDATE、USER、VALIDATE、VALUES、VARCHAR、VARCHAR2、VIEW、WHENEVER、WHERE、WITH"
						.split("、"))));
		DATABASE_KEY.put(DataBaseType.RDBMS, DATABASE_KEY.get(DataBaseType.Oracle));
		Set<DataBaseType> keys = DATABASE_KEY.keySet();
		Set<String> keyFields;
		List<String> lowerKeyFields;
		for (DataBaseType key : keys) {
			keyFields = DATABASE_KEY.get(key);
			lowerKeyFields = new ArrayList<String>(keyFields.size());
			for (String keyField : keyFields) {
				lowerKeyFields.add(keyField.toLowerCase());
			}
			keyFields.addAll(lowerKeyFields);
		}
	}

	DataBaseType(String typeName, String driverClassName) {
		this.typeName = typeName;
		this.driverClassName = driverClassName;
	}

	public String getDriverClassName() {
		return this.driverClassName;
	}

	public String appendJDBCSuffixForReader(String jdbc) {
		String result = jdbc;
		String suffix = null;
		switch (this) {
		case MySql:
		case DRDS:
			suffix = "yearIsDateType=false&zeroDateTimeBehavior=convertToNull&tinyInt1isBit=false&rewriteBatchedStatements=true";
			if (jdbc.contains("?")) {
				result = jdbc + "&" + suffix;
			} else {
				result = jdbc + "?" + suffix;
			}
			break;
		case Oracle:
			break;
		case SQLServer:
			break;
		case DB2:
			break;
		case PostgreSQL:
			break;
		case ClickHouse:
			break;
		case RDBMS:
			break;
		case KingbaseES:
			break;
		case Oscar:
			break;
		default:
			throw TransferException.as(DBUtilErrorCode.UNSUPPORTED_TYPE, "unsupported database type.");
		}

		return result;
	}

	public String appendJDBCSuffixForWriter(String jdbc) {
		String result = jdbc;
		String suffix = null;
		switch (this) {
		case MySql:
			suffix = "yearIsDateType=false&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=true&tinyInt1isBit=false";
			if (jdbc.contains("?")) {
				result = jdbc + "&" + suffix;
			} else {
				result = jdbc + "?" + suffix;
			}
			break;
		case DRDS:
			suffix = "yearIsDateType=false&zeroDateTimeBehavior=convertToNull";
			if (jdbc.contains("?")) {
				result = jdbc + "&" + suffix;
			} else {
				result = jdbc + "?" + suffix;
			}
			break;
		case Oracle:
			break;
		case SQLServer:
			break;
		case DB2:
			break;
		case PostgreSQL:
			break;
		case ClickHouse:
			break;
		case RDBMS:
			break;
		case KingbaseES:
			break;
		case Oscar:
			break;
		default:
			throw TransferException.as(DBUtilErrorCode.UNSUPPORTED_TYPE, "unsupported database type.");
		}

		return result;
	}

	public String formatPk(String splitPk) {
		String result = splitPk;

		switch (this) {
		case MySql:
		case Oracle:
			if (splitPk.length() >= 2 && splitPk.startsWith("`") && splitPk.endsWith("`")) {
				result = splitPk.substring(1, splitPk.length() - 1).toLowerCase();
			}
			break;
		case SQLServer:
			if (splitPk.length() >= 2 && splitPk.startsWith("[") && splitPk.endsWith("]")) {
				result = splitPk.substring(1, splitPk.length() - 1).toLowerCase();
			}
			break;
		case DB2:
		case PostgreSQL:
		case KingbaseES:
		case Oscar:
			break;
		default:
			throw TransferException.as(DBUtilErrorCode.UNSUPPORTED_TYPE, "unsupported database type.");
		}

		return result;
	}

	public String quoteColumnName(String columnName) {
		String result = columnName;

		switch (this) {
		case MySql:
			result = "`" + columnName.replace("`", "``") + "`";
			break;
		case Oracle:
			break;
		case SQLServer:
			result = "[" + columnName + "]";
			break;
		case DB2:
		case PostgreSQL:
		case KingbaseES:
		case Oscar:
			break;
		default:
			throw TransferException.as(DBUtilErrorCode.UNSUPPORTED_TYPE, "unsupported database type");
		}

		return result;
	}

	public String quoteTableName(String tableName) {
		String result = tableName;

		switch (this) {
		case MySql:
			result = "`" + tableName.replace("`", "``") + "`";
			break;
		case Oracle:
			break;
		case SQLServer:
			break;
		case DB2:
			break;
		case PostgreSQL:
			break;
		case KingbaseES:
			break;
		case Oscar:
			break;
		default:
			throw TransferException.as(DBUtilErrorCode.UNSUPPORTED_TYPE, "unsupported database type");
		}

		return result;
	}

	private static Pattern mysqlPattern = Pattern.compile("jdbc:mysql://(.+):\\d+/.+");
	private static Pattern oraclePattern = Pattern.compile("jdbc:oracle:thin:@(.+):\\d+:.+");

	/**
	 * 注意：目前只实现了从 mysql/oracle 中识别出ip 信息.未识别到则返回 null.
	 */
	public static String parseIpFromJdbcUrl(String jdbcUrl) {
		Matcher mysql = mysqlPattern.matcher(jdbcUrl);
		if (mysql.matches()) {
			return mysql.group(1);
		}
		Matcher oracle = oraclePattern.matcher(jdbcUrl);
		if (oracle.matches()) {
			return oracle.group(1);
		}
		return null;
	}

	/**
	 * 获取数据库的转义
	 * 
	 * @param dataBaseType
	 * @return
	 */
	public static String getKeyMeaning(DataBaseType dataBaseType) {
		switch (dataBaseType) {
		case MySql:

			return "`";

		default:
			return "\"";
		}
	}

	/**
	 * 转义所有字段
	 * 
	 * @param dataBaseType
	 * @param cloumns
	 * @return
	 */
	public static List<String> castKeyFields(DataBaseType dataBaseType, List<String> cloumns) {
		Set<String> keys = DATABASE_KEY.get(dataBaseType);
		if (keys == null) {
			keys = DATABASE_KEY.get(DataBaseType.RDBMS);
		}
		String keyMeaning = getKeyMeaning(dataBaseType);
		String cloumn;
		for (int i = 0; i < cloumns.size(); i++) {
			cloumn = cloumns.get(i);
			if (keys.contains(cloumn) || cloumn.contains("-")) {
				cloumns.set(i, keyMeaning + cloumn + keyMeaning);
			}
		}
		return cloumns;
	}

	/**
	 * 转义所有字段
	 * 
	 * @param dataBaseType
	 * @param cloumns
	 * @return
	 */
	public static String castKeyFieldsAndJoin(DataBaseType dataBaseType, List<String> cloumns, String separator) {
		Set<String> keys = DATABASE_KEY.get(dataBaseType);
		if (keys == null) {
			keys = DATABASE_KEY.get(DataBaseType.RDBMS);
		}
		String keyMeaning = getKeyMeaning(dataBaseType);
		String cloumn;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cloumns.size(); i++) {
			cloumn = cloumns.get(i);
			if (i != 0) {
				sb.append(separator);
			}
			if (keys.contains(cloumn) || cloumn.contains("-")) {
				sb.append(keyMeaning + cloumn + keyMeaning);
			} else if (cloumn.contains(" AS ") || cloumn.contains(" as ")) {

				String[] asCloumn = null;
				if (cloumn.contains(" AS ")) {
					asCloumn = cloumn.split(" AS ");
				}
				if (cloumn.contains(" as ")) {
					asCloumn = cloumn.split(" as ");
				}
				if (keys.contains(asCloumn[0].trim()) || asCloumn[0].contains("-")) {
					sb.append(keyMeaning + asCloumn[0].trim() + keyMeaning + " AS " + asCloumn[1]);
				} else {
					sb.append(cloumn);
				}
			} else {
				sb.append(cloumn);
			}
		}
		return sb.toString();
	}

	/**
	 * 获取不能作为字段的关键字
	 * 
	 * @param dataBaseType
	 * @return
	 */
	public static Set<String> getKeys(DataBaseType dataBaseType) {
		Set<String> keys = DATABASE_KEY.get(dataBaseType);
		if (keys == null) {
			keys = DATABASE_KEY.get(DataBaseType.RDBMS);
		}
		return keys;
	}

	/**
	 * 识别key转换成可以用的字段
	 * 
	 * @param keys
	 * @param keyMeaning
	 * @param cloumn
	 * @return
	 */
	public static String castKeyField(Set<String> keys, String keyMeaning, String cloumn) {

		if (keys.contains(cloumn) || cloumn.contains("-")) {
			return keyMeaning + cloumn + keyMeaning;
		}

		return cloumn;
	}

	/**
	 * 根据jdbcurl获取数据库类型 当没有的时候默认返回rdbms
	 * 
	 * @param jdbcUrl
	 * @return
	 */
	public static DataBaseType getDataBaseTypeByJdbcUrl(String jdbcUrl) {
		// 取第二个冒号结尾
		try {
			String pre = jdbcUrl.substring(jdbcUrl.indexOf("jdbc:") + 5, jdbcUrl.indexOf(":", 5));
			switch (pre) {
			case "mysql":
				return MySql;
			case "oracle":
				return Oracle;
			case "jtds":
			case "sqlserver":
				return SQLServer;
			case "db2":
				return DB2;
			case "kingbasees":
				return KingbaseES;
			case "clickhouse":
				return ClickHouse;
			case "dm":
				return DM;
			case "postgresql":
				return PostgreSQL;
			default:
				return RDBMS;
			}
		} catch (Exception e) {
			return RDBMS;
		}

	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}
