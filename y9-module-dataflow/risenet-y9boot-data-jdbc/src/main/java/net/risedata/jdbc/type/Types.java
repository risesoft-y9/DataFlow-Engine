package net.risedata.jdbc.type;

import java.util.ArrayList;
import java.util.List;

import net.risedata.jdbc.type.sql.SqlType;
import net.risedata.jdbc.type.sql.impl.DateSqlTypeImpl;
import net.risedata.jdbc.type.sql.impl.NumberSqlTypeImpl;
import net.risedata.jdbc.type.sql.impl.StringSqlTypeImpl;
import net.risedata.jdbc.type.sql.impl.TimeSqlTypeImpl;
/**
 * 管理 数据库类型 与java类型的类型处理
 * @author libo
 *2021年2月8日
 */
public class Types {
    private static final List<SqlType> SQL_TYPES = new ArrayList<>();
    static {
    	SQL_TYPES.add(new StringSqlTypeImpl());
    	SQL_TYPES.add(new DateSqlTypeImpl());
    	SQL_TYPES.add(new NumberSqlTypeImpl());
    	SQL_TYPES.add(new TimeSqlTypeImpl());
    	
    }
	public static Class<?> getSqlType(String type) {
        for (SqlType sqlType : SQL_TYPES) {
			if (sqlType.isHandle(type)) {
				return sqlType.getType(type);
			}
		}
		return null;
	}

}
