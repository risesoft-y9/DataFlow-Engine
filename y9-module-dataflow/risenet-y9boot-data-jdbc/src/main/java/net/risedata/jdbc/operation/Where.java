package net.risedata.jdbc.operation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.config.model.JoinConfig;
import net.risedata.jdbc.operation.impl.SimpleOperation;
import net.risedata.jdbc.search.exception.FieldException;

/**
 * 包含此次执行的全部数据
 * 
 * @author libo 2020年10月21日
 */
public class Where {
	private StringBuilder sql;
	private List<Object> args;
	private Map<String, Object> excludeMap;
	private FieldConfig fc;
	private Map<String, Object> valueMap;
	private BeanConfig bc;

	public Where(StringBuilder sql, FieldConfig fc, Map<String, Object> valueMap, List<Object> args,
			Map<String, Object> excludeMap, BeanConfig bc) {
		this.excludeMap = excludeMap;
		this.sql = sql;
		this.fc = fc;
		this.valueMap = valueMap;
		this.args = args;
		this.bc = bc;

	}

	/**
	 * 拿到当前的bean配置
	 * 
	 * @return
	 */
	public Class<?> getBeanClass() {
		return bc.getCla();
	}

	/**
	 * 拿到当前的字段配置
	 * 
	 * @return
	 */
	public FieldConfig getFieldConfig() {
		return fc;
	}

	/**
	 * 根据 字段名拿到对应的cloum名字
	 * 
	 * @param fieldName
	 * @return
	 */
	public String getCloum(String fieldName) {
		FieldConfig fc = bc.getField(fieldName);
		if (fc == null) {
			throw new FieldException("fieldName" + fieldName + "在 bean" + bc.getCla() + "中未找到");
		}
		return fc.getColumn();
	}

	/**
	 * 添加一个参数到最后执行的jdbc操作里面
	 * 
	 * @param args
	 * @return
	 */
	public Where add(Object args) {
		this.args.add(args);
		return this;
	}

	public Where in(@SuppressWarnings("rawtypes") Collection values, boolean bracket) {
		if (bracket) {
			sql.append("(");
		}
		boolean flag = false;
		for (Object object : values) {
			if (flag) {
				sql.append(",?");
			} else {
				flag = true;
				sql.append("?");
			}
			add(object);
		}
		if (bracket) {
			sql.append(")");
		}

		return this;
	}
	
	public Where in(@SuppressWarnings("rawtypes") Object[] values, boolean bracket) {
		if (bracket) {
			sql.append("(");
		}
		boolean flag = false;
		for (Object object : values) {
			if (flag) {
				sql.append(",?");
			} else {
				flag = true;
				sql.append("?");
			}
			add(object);
		}
		if (bracket) {
			sql.append(")");
		}

		return this;
	}
	public Where in(@SuppressWarnings("rawtypes") Collection values) {
		return in(values,true);
	}
	/**
	 * 根据连接的bean 拿到对应连接的config 注意:如果不存在则没有
	 * 
	 * @param joinEntiry
	 * @return
	 */
	public JoinConfig getJoinConfig(Class<?> joinEntiry) {
		return bc.getJoin(joinEntiry);
	}

	/**
	 * 拿到当前表的别名
	 * 
	 * @return
	 */
	public String getTableAs() {
		return bc.getTableAs();
	}

	/**
	 * 拿到拼接的sql 注意 不用加and 返回值是true 会根据条件增加and
	 * 
	 * @return the sql
	 */
	public StringBuilder getSql() {
		return sql;
	}

	/**
	 * 追加到sql中
	 * 
	 * @param seq
	 * @return
	 */
	public StringBuilder append(CharSequence seq) {
		return sql.append(seq);
	}

	/**
	 * 拿到当前字段的值
	 * 
	 * @return the value
	 */
	public Object getValue() {
		return SimpleOperation.getValue(fc, valueMap);
	}

	/**
	 * 拿到某一个字段的值
	 * 
	 * @return the value
	 */
	public Object getValue(String fieldName) {
		return valueMap.get(fieldName);
	}

	/**
	 * @return the args
	 */
	public List<Object> getArgs() {
		return args;
	}

	/**
	 * 添加到排除继续验证操作的map中 注意必须把此操作的级别调整到最高才有效
	 * 
	 * @param key
	 */
	public void removeExcludeField(String key) {
		excludeMap.remove(key);
	}
}
