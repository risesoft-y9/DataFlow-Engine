package net.risesoft.util;

/**
 * 数据源工具类
 * @author pzx
 *
 */
public class DataConstant {
	
	public static final String ES = "elasticsearch";
	
	public static final String MYSQL = "mysql";
	
	public static final String ORACLE = "oracle";
	
	public static final String DM = "dm";
	
	public static final String PG = "postgresql";
	
	public static final String KINGBASE = "kingbase";
	
	public static final String FTP = "ftp";
	
	public static String getDirver(String type) {
		switch (type) {
			case MYSQL:
				return "com.mysql.cj.jdbc.Driver";
			case ORACLE:
				return "oracle.jdbc.OracleDriver";
			case PG:
				return "org.postgresql.Driver";
			case KINGBASE:
				return "com.kingbase8.Driver";
			case DM:
				return "dm.jdbc.driver.DmDriver";
			default:
				return "";
		}
	}
}
