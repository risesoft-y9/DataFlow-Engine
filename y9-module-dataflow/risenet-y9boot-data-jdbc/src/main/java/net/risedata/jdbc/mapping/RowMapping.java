package net.risedata.jdbc.mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.exception.SqlExecutionException;
import net.risedata.jdbc.executor.set.SetValueExecutor;
import net.risedata.jdbc.executor.set.impl.FieldSetValueExecutor;

/**
 * 执行映射
 * 
 * @author libo 2021年2月7日
 * @param <T>
 */
public class RowMapping<T> implements RowMapper<T> {
	private Collection<FieldConfig> fields;
	private BeanConfig bc;
	private int[] isCloum;
	private LRowMapping<T> rowMapping;
	private static final SetValueExecutor<Field> SET_VALUE = new FieldSetValueExecutor();

	public RowMapping(BeanConfig bc, Collection<FieldConfig> fields) {
		this.fields = fields;
		isCloum = new int[fields.size()];
		this.bc = bc;
	}

	public RowMapping(BeanConfig bc, Collection<FieldConfig> fields, LRowMapping<T> roMapping) {
		this(bc, fields);
		this.rowMapping = roMapping;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		int index = 0;
		if (rowNum == 0) {
			for (FieldConfig fieldConfig : fields) {
				if (fieldConfig.isEntiry()) {
					break;
				}
				isCloum[index] = isExistColumn(rs, fieldConfig.getColumn()) ? 0 : 1;
				index++;
			}
		}
		index = -1;
		Object o = null;
		try {
			o = bc.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		T ret = (T) o;
		@SuppressWarnings("rawtypes")
		HandleMapping hand = null;
		try {
			for (FieldConfig fieldConfig : fields) {
				index++;
				if (isCloum[index] == 1) {
					continue;
				}
				hand = fieldConfig.getHandle();
				try {
					hand.handle(ret, fieldConfig.getSetValueField(), SET_VALUE, fieldConfig.getFieldType(),
							hand.getValue(rs, fieldConfig.getColumn()));
				} catch (SQLException e) {
					throw new SqlExecutionException(
							"出现sql异常 " + e.getMessage() + "  对应的字段是: " + fieldConfig.getColumn());
				}
			}
			if (rowMapping != null) {
				rowMapping.handle(ret, rs, rowNum);
			}
			if (bc.columnMappings()!=null) {
				List<ColumnMapping> lists = bc.columnMappings();
				for (ColumnMapping columnMapping : lists) {
					columnMapping.handle(ret, bc);
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static String ClobToString(Clob clob) throws SQLException, IOException {
		String reString = "";
		Reader is = clob.getCharacterStream();
		BufferedReader br = new BufferedReader(is);
		String s = br.readLine();
		StringBuffer sb = new StringBuffer();
		while (s != null) {
			sb.append(s);
			s = br.readLine();
		}
		reString = sb.toString();
		if (br != null) {
			br.close();
		}
		if (is != null) {
			is.close();
		}
		return reString;
	}

	/**
	 * 判断查询结果集中是否存在某列
	 * 
	 * @param rs         查询结果集
	 * @param columnName 列名
	 * @return true 存在; false 不存咋
	 */
	public boolean isExistColumn(ResultSet rs, String columnName) {
		try {
			if (rs.findColumn(columnName) > 0) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		}

		return false;
	};

}
