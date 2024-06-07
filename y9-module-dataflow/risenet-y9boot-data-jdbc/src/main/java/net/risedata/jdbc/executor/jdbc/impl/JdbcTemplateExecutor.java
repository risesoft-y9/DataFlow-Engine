package net.risedata.jdbc.executor.jdbc.impl;

import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.executor.jdbc.JdbcExecutor;
import net.risedata.jdbc.factory.BeanConfigFactory;
import net.risedata.jdbc.mapping.RowMapping;

/**
 * 使用 spring jdbc 实现的 执行器
 * 
 * @author libo 2021年2月8日
 */
public class JdbcTemplateExecutor implements JdbcExecutor {
	private JdbcTemplate jt;

	public JdbcTemplateExecutor(JdbcTemplate jt) {
		this.jt = jt;
	}

	@Override
	public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
		try {
			return jt.queryForObject(sql, args, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public <T> T queryForSimpleObject(String sql, Class<T> requiredType, Object... args) {
		try {
			return jt.queryForObject(sql, args, requiredType);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public <T> List<T> queryForSimpleList(String sql, Class<T> elementType, Object... args) {
		return jt.queryForList(sql, args, elementType);
	}

	@Override
	public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) {
		return jt.query(sql, args, rowMapper);
	}

	@Override
	public Integer update(String sql, Object... args) {
		return jt.update(sql, args);
	}

	@Override
	public int[] batchUpdate(String sql, List<Object[]> batchArgs) {

		return jt.batchUpdate(sql, batchArgs);
	}

	@Override
	public Map<String, Object> queryForMap(String sql, Object... args) {
		return jt.queryForMap(sql, args);
	}

	@Override
	public List<Map<String, Object>> queryForListMap(String sql, Object... args) {
		return jt.queryForList(sql, args);
	}

	@Override
	public Map<String, Object> queryForMap(String sql) {
		return jt.queryForMap(sql);
	}

	@Override
	public <T> T queryForObject(String sql, Class<T> returnType) {
		return jt.queryForObject(sql, returnType);
	}

	@Override
	public int[] batchUpdate(String[] array) {
		return jt.batchUpdate(array);
	}

	@Override
	public <T> T queryForObject(String sql, Class<T> returnType, Object... args) {
		BeanConfig beanConfig = BeanConfigFactory.getInstance(returnType);
		if (beanConfig == null) {
			return queryForSimpleObject(sql, returnType, args);
		}
		return queryForObject(sql, new RowMapping<>(beanConfig, beanConfig.getAllFields()), args);
	}

	@Override
	public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) {
		BeanConfig beanConfig = BeanConfigFactory.getInstance(elementType);
		if (beanConfig == null) {
			return queryForSimpleList(sql, elementType, args);
		}
		return queryForList(sql, new RowMapping<>(beanConfig, beanConfig.getAllFields()), args);
	}

	@Override
	public <T> List<T> queryForList(String sql, Class<T> elementType) {
		return queryForList(sql, elementType, new Object[] {});
	}

	@Override
	public Integer update(String sql) {
		return update(sql,new Object[] {});
	}

}
