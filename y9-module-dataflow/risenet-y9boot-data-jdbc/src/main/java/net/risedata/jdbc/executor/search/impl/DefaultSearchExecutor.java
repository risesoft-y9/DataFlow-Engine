package net.risedata.jdbc.executor.search.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.validation.constraints.NotNull;

import org.springframework.dao.EmptyResultDataAccessException;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.config.JdbcConfig;
import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.config.model.JoinConfig;
import net.risedata.jdbc.exception.ConfigException;
import net.risedata.jdbc.exception.SqlExecutionException;
import net.risedata.jdbc.executor.jdbc.JDBC;
import net.risedata.jdbc.executor.jdbc.JdbcExecutor;
import net.risedata.jdbc.executor.page.PageExecutor;
import net.risedata.jdbc.executor.search.SearchExecutor;
import net.risedata.jdbc.mapping.LRowMapping;
import net.risedata.jdbc.mapping.RowMapping;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.search.LPageable;
import net.risedata.jdbc.search.LSort;
import net.risedata.jdbc.search.Order;
import net.risedata.jdbc.utils.Sqlbuilder;

/**
 * 执行查询操作的默认执行操作类
 * 
 * @author libo 2021年2月10日
 */
public class DefaultSearchExecutor extends JDBC implements SearchExecutor {
	/**
	 * sql执行器
	 */
	private JdbcExecutor jdbcExecutor;

	private PageExecutor pageExecutor;

	public DefaultSearchExecutor(JdbcExecutor jdbcExecutor, PageExecutor pageExecutor) {
		if (pageExecutor == null) {
			throw new NullPointerException("pageExecutor is null");
		}
		this.jdbcExecutor = jdbcExecutor;
		this.pageExecutor = pageExecutor;
	}

	/**
	 * 创建排序语句
	 * 
	 * @param orders 排序的字段
	 * @param sql    sql语句
	 * @param config 配置
	 */
	protected static void createOrders(List<Order> orders, StringBuilder sql, BeanConfig config,
			Map<String, Object> valueMap) {
		if (orders.size() > 0) {
			Order o = null;
			String column = null;
			FieldConfig fc = null;
			boolean flag = true;
			Map<String, Object> orderKeys = new HashMap<>();
			// 不使用order?
			for (int i = 0; i < orders.size(); i++) {
				o = orders.get(i);
				if (!orderKeys.containsKey(o.getField()) && o.If(valueMap)) {
					orderKeys.put(o.getField(), '1');
					fc = config.getField(o.getField());
					column = fc != null ? fc.getColumn() : o.getField();
					if (flag) {
						sql.append(" ORDER BY " + column + " " + o.getOrder());
						flag = false;
					} else {
						sql.append(" , " + column + " " + o.getOrder());
					}
				}
			}
		}
	}

	/**
	 * 解析分页
	 * 
	 * @param sql
	 * @param page
	 * @param entiry
	 * @param bc
	 * @return
	 */
	protected static String parsePage(PageExecutor pageExecutor, StringBuilder sql, LPageable page, Object entiry,
			BeanConfig bc, Map<String, Object> valueMap, boolean isOrder) {
		if (isOrder) {

			LSort s = page.getSort();
			if (s != null) {
				List<Order> orders = s.getOrders();
				createOrders(orders, sql, bc, valueMap);
			}
		}

		return pageExecutor.getPageSql(sql.toString(), page.getPageNo(), page.getPageSize());
	}

	/**
	 * 查询 结果返回 list
	 * 
	 * @param <T>
	 * @param sql           sql
	 * @param args          参数
	 * @param returnType    返回值类型
	 * @param fieldsConfigs 格外的映射处理配置
	 * @param IsTransient   是否加载 添加Transient注解的字段
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <T> List<T> queryForList(JdbcExecutor jdbcExecutor, String sql, Object[] args, Class<T> returnType,
			Collection<FieldConfig> fieldConfigs, boolean IsTransient, LRowMapping<T> romapping, BeanConfig bc) {
		try {
			if (bc != null && bc.getCla() != returnType) {
				bc = getConfig(returnType);
			}
			if (bc == null) {
				if (returnType == Map.class || returnType.isAssignableFrom(Map.class)) {
					return (List<T>) jdbcExecutor.queryForListMap(sql, args);
				}
				return jdbcExecutor.queryForSimpleList(sql, returnType, args);
			}
			if (fieldConfigs != null) {
				fieldConfigs.addAll(bc.getRowMappingList());
				return jdbcExecutor.queryForList(sql, new RowMapping<T>(bc, fieldConfigs, romapping), args);
			}

			if (IsTransient) {
				return jdbcExecutor.queryForList(sql, new RowMapping<T>(bc, bc.getAllFields(), romapping), args);
			}
			return jdbcExecutor.queryForList(sql, new RowMapping<T>(bc, bc.getFieldlist(), romapping), args);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SqlExecutionException("执行sql 出现异常sql:" + sql);

		}
	}

	/**
	 * 
	 * @param sql
	 * @param bc
	 * @param fieldConfigs 用来做映射的fieldconfigs 返回哪些field被作用于连接用于
	 */
	protected static List<FieldConfig> createJoinSql(StringBuilder sql, BeanConfig bc, Map<String, Object> valueMap,
			boolean isAll) {
		List<JoinConfig> jcs = bc.getJoins();
		if (jcs == null) {
			return null;
		}
		List<FieldConfig> fieldConfigs = null;
		int insertIndex = BeanConfig.SELECT_INSERT_INDEX;
		for (JoinConfig joinConfig : jcs) {
			if (joinConfig.isJoin(valueMap)) {
				if (fieldConfigs == null) {

					fieldConfigs = new ArrayList<>();
				}
				fieldConfigs.addAll(joinConfig.getFields());
				if (joinConfig.isFunction()) {// 聚合函数 插入 聚合模式
					sql.insert(insertIndex, joinConfig.getSql());
					insertIndex += joinConfig.getSql().length();
				} else { // 非聚合模式下table from 区间中的查询不要不要
					sql.insert(insertIndex, joinConfig.getFieldSql() + ",");
					sql.append(joinConfig.getSql());
				}
			}
		}
		return fieldConfigs;
	}

	public JdbcExecutor getJdbcExecutor() {
		return jdbcExecutor;
	}

	public void setJdbcExecutor(JdbcExecutor jdbcExecutor) {
		this.jdbcExecutor = jdbcExecutor;
	}

	@Override
	public <T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, boolean isOrder, boolean isNoJoin,
			LRowMapping<T> rowmapping, LSort sort, Class<T> returnType) {
		List<Object> args = new ArrayList<>();
		BeanConfig bc = getConfig(id);
		String selectsql = bc.getSelectTableSql(field, isTransient);
		StringBuilder sql = new StringBuilder(selectsql);
		valueMap = createValueMap(id, valueMap, bc.getFieldlist(), sql);
		List<FieldConfig> fields = null;
		if (!isNoJoin) {
			fields = createJoinSql(sql, bc, valueMap, isTransient);
		}

		createWhereSql(bc, sql, bc.getFieldlist(), args, operation, valueMap);
		if (isOrder && sort != null) {
			createOrders(doOrders(bc.getOrders(), sort.getOrders()), sql, bc, valueMap);
		} else if (isOrder) {
			createOrders(bc.getOrders(), sql, bc, valueMap);
		} else if (sort != null) {
			createOrders(sort.getOrders(), sql, bc, valueMap);
		}
		String selectSql = sql.toString();
		return queryForList(jdbcExecutor, selectSql, args.toArray(), returnType, fields, isTransient, rowmapping, bc);
	}

	private List<Order> doOrders(List<Order> base, List<Order> orders) {
		if (base == null) {
			Collections.sort(orders);// 重新排序
			return orders;
		} else {
			HashSet<Order> sets = new HashSet<>();
			sets.addAll(base);
			sets.addAll(orders);
			Collections.sort(orders);
			return Arrays.asList(sets.toArray(new Order[sets.size()]));
		}
	}

	@Override
	public <T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, LRowMapping<T> rowmapping, Class<T> returnType) {
		return searchForList(id, field, operation, valueMap, isTransient, true, rowmapping, returnType);
	}

	@Override
	public <T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, LRowMapping<T> rowmapping, Class<T> returnType) {
		return searchForList(id, field, operation, valueMap, false, rowmapping, returnType);
	}

	@Override
	public <T> List<T> searchForList(@NotNull Object id, Map<String, Operation> operation, Map<String, Object> valueMap,
			LRowMapping<T> rowmapping, Class<T> returnType) {
		return searchForList(id, null, operation, valueMap, rowmapping, returnType);
	}

	@Override
	public <T> List<T> searchForList(@NotNull Object id, Map<String, Operation> operation, Map<String, Object> valueMap,
			Class<T> returnType) {
		return searchForList(id, operation, valueMap, null, returnType);
	}

	@Override
	public <T> List<T> searchForList(@NotNull Object id, Map<String, Operation> operation, Class<T> returnType) {
		return searchForList(id, operation, null, returnType);
	}

	@Override
	public <T> List<T> searchForList(@NotNull Object id, Class<T> returnType) {
		return searchForList(id, null, returnType);
	}

	@Override
	public <T> LPage<T> searchForPage(Object id, String field, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, boolean isOrder, LRowMapping<T> rowmapping,
			Class<T> returnType) {
		List<Object> args = new ArrayList<>();
		BeanConfig bc = getConfig(id);
		StringBuilder sql = new StringBuilder(bc.getSelectTableSql(field, isTransient));
		valueMap = createValueMap(id, valueMap, bc.getFieldlist(), sql);
		List<FieldConfig> fieldsConfigs = createJoinSql(sql, bc, valueMap, isTransient);// 连接sql
		// 此时拿到了连接的config就应该将select 的部分进行追加

		createWhereSql(bc, sql, bc.getFieldlist(), args, operation, valueMap);
		return toPage(jdbcExecutor, pageExecutor, sql, pageable, id, args.toArray(), returnType, bc, fieldsConfigs,
				isTransient, isOrder, valueMap, rowmapping);
	}

	/**
	 * 处理 sort的排序以及去重
	 * 
	 * @param bc
	 * @param page
	 */
	private static void joinPageOrders(BeanConfig bc, LPageable page) {
		List<Order> orders = null;// 全新的用于排序的
		HashSet<Order> sets = new HashSet<>();
		if (page.getSort() != null) { // page里面的order优先级最高 这一步是将自定义的order 和 后面追加的order 去重
			sets.addAll(bc.getOrders());
			sets.addAll(page.getSort().getOrders());
			orders = Arrays.asList(sets.toArray(new Order[sets.size()]));
			Collections.sort(orders);// 重新排序
		} else {// 直接使用beanconfig的
			orders = bc.getOrders();
		}
		LSort sort = page.getSort();
		if (sort == null) {
			sort = LSort.toSort(orders);
		} else {
			sort.setOrders(orders);
		}
		// 重新设置sort
		page.setSort(sort);
	}

	private static final String COUNT_STR = "SELECT COUNT(1) COUNT ";

	/**
	 * 将组织好wheresql 的语句进行分页处理
	 * 
	 * @param <T>
	 * @param whereSql sql
	 * @param page     page对象
	 * @param entiry   entiry
	 * @param args     参数
	 * @param cla      class也是最后返回的对象
	 * @return
	 */
	public static <T> LPage<T> toPage(JdbcExecutor jdbcExecutor, PageExecutor pageExecutor, StringBuilder sql,
			LPageable page, Object entiry, Object[] args, Class<T> cla, BeanConfig bc, List<FieldConfig> fields,
			boolean isTransient, boolean isOrder, Map<String, Object> valueMap, LRowMapping<T> rowmapping) {

		joinPageOrders(bc, page);
		int groupIndex = sql.indexOf(" GROUP BY ");
		// count 和group 不能同时出现如果同时出现则需要换
		String tempCountSql = null;
		if (groupIndex != -1) {
			tempCountSql = COUNT_STR + " FROM ( " + sql + " ) ";
		} else {
			tempCountSql = COUNT_STR + sql.substring(sql.indexOf(bc.getTableFrom()), sql.length());
		}
		final String countSql = tempCountSql;
		String selectSql = parsePage(pageExecutor, sql, page, entiry, bc, valueMap, isOrder);
		LPage<T> mypage = new LPage<>();
		mypage.setPagesize(page.getPageSize());
		mypage.setPageno(page.getPageNo());
		// TODO 当count没有条件的时候同步更快
		if (bc.isSync() && args.length > 0) {
			Future<Long> future = JdbcConfig.SYNC.submit(() -> {
				return jdbcExecutor.queryForSimpleObject(countSql, long.class, args);
			});
			mypage.setContent(queryForList(jdbcExecutor, selectSql, args, cla, fields, isTransient, rowmapping, bc));
			if (page.getPageNo() <= 1 && mypage.getContent().size() < page.getPageSize()) {
				mypage.setTotal(mypage.getContent().size());
			} else {
				try {
					mypage.setTotal(future.get());
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			}
		} else {
			mypage.setContent(queryForList(jdbcExecutor, selectSql, args, cla, fields, isTransient, rowmapping, bc));
			if (page.getPageNo() <= 1 && mypage.getContent().size() < page.getPageSize()) {
				mypage.setTotal(mypage.getContent().size());
			} else {
				mypage.setTotal(jdbcExecutor.queryForSimpleObject(countSql, long.class, args));
			}
		}
		return mypage;
	}

	/**
	 * 将组织好wheresql 的语句进行分页处理
	 * 
	 * @param <T>
	 * @param whereSql sql
	 * @param page     page对象
	 * @param entiry   entiry
	 * @param args     参数
	 * @return
	 */
	public static LPage<Map<String, Object>> toPage(JdbcExecutor jdbcExecutor, PageExecutor pageExecutor,
			StringBuilder sql, String whereSql, LPageable page, Object entiry, Object[] args, BeanConfig bc,
			List<FieldConfig> fields, Map<String, Object> valueMap, boolean isOrder,boolean isGroup) {
		joinPageOrders(bc, page);
		String countSql=null;
		//TODO 此处需要改掉
		if (isGroup) {
			 countSql ="select count(*) COUNT " + whereSql;
		}else {
			 countSql = bc.getCountTableSql() + whereSql;
		}
		String selecsql = parsePage(pageExecutor, sql, page, entiry, bc, valueMap, isOrder);

		
		LPage<Map<String, Object>> mypage = new LPage<>();
		mypage.setPagesize(page.getPageSize());
		mypage.setPageno(page.getPageNo());
		mypage.setContent(jdbcExecutor.queryForListMap(selecsql, args));
		mypage.setTotal(jdbcExecutor.queryForSimpleObject(countSql, long.class, args));
		return mypage;
	}

	@Override
	public <T> LPage<T> searchForPage(Object id, String field, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, LRowMapping<T> rowmapping, Class<T> returnType) {
		return searchForPage(id, field, pageable, operation, valueMap, isTransient, true, rowmapping, returnType);
	}

	@Override
	public <T> LPage<T> searchForPage(Object id, String field, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, LRowMapping<T> rowmapping, Class<T> returnType) {
		return searchForPage(id, field, pageable, operation, valueMap, false, rowmapping, returnType);
	}

	@Override
	public <T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, LRowMapping<T> rowmapping, Class<T> returnType) {
		return searchForPage(id, null, pageable, operation, valueMap, rowmapping, returnType);
	}

	@Override
	public <T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, Class<T> returnType) {
		return searchForPage(id, pageable, operation, valueMap, null, returnType);
	}

	@Override
	public <T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Class<T> returnType) {
		return searchForPage(id, pageable, operation, null, returnType);
	}

	@Override
	public <T> LPage<T> searchForPage(Object id, LPageable pageable, Class<T> returnType) {
		return searchForPage(id, pageable, null, returnType);
	}

	@Override
	public <T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type,
			Object[] args, boolean isTransient, LRowMapping<T> rowMapping) {
		String countSql = getCountSql(whereSql);
		appendOrderSql(sql, page);
		String sreachSql = pageExecutor.getPageSql(sql.toString(), page.getPageNo(), page.getPageSize());
		LPage<T> mypage = new LPage<>();
		mypage.setPagesize(page.getPageSize());
		mypage.setPageno(page.getPageNo());
		mypage.setContent(queryForList(jdbcExecutor, sreachSql, args, type, null, isTransient, rowMapping, null));
		mypage.setTotal(jdbcExecutor.queryForObject(countSql, Integer.class));
		return mypage;
	}

	public LPage<Map<String, Object>> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page,
			Object[] args) {
		String countSql = getCountSql(whereSql);
		appendOrderSql(sql, page);
		String sreachSql = pageExecutor.getPageSql(sql.toString(), page.getPageNo(), page.getPageSize());
		LPage<Map<String, Object>> mypage = new LPage<>();
		mypage.setPagesize(page.getPageSize());
		mypage.setPageno(page.getPageNo());
		mypage.setContent(jdbcExecutor.queryForListMap(sreachSql, args));
		mypage.setTotal(jdbcExecutor.queryForObject(countSql, Integer.class));
		return mypage;
	}

	private void appendOrderSql(StringBuilder sql, LPageable page) {
		LSort s = page.getSort();
		if (s != null) {
			List<Order> orders = s.getOrders();
			if (orders.size() > 0) {
				Order o = null;
				for (int i = 0; i < orders.size(); i++) {// 第一个已经使用所以con第二个开始
					o = orders.get(i);
					if (i == 0) {
						sql.append(" ORDER BY " + o.getField() + " " + o.getOrder());
					} else {
						sql.append(" , " + o.getField() + " " + o.getOrder());
					}
				}
			}
		}
	}

	private String getCountSql(String whereSql) {
		return "SELECT count(1) COUNT FROM" + whereSql;
	}

	@Override
	public <T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type,
			Object[] args, LRowMapping<T> rowMapping) {
		return searchSqlForPage(sql, whereSql, page, type, args, false, rowMapping);
	}

	@Override
	public <T> T searchField(Object entiry, String field, Class<T> returnType, Map<String, Operation> operationMap,
			Map<String, Object> valueMap) {
		BeanConfig bc = getConfig(entiry);
		FieldConfig fc = bc.getField(field);// 拿到field的config
		String cloum = field;
		if (fc != null) {
			cloum = fc.getColumn();
		}
		StringBuilder sql = new StringBuilder(bc.getSelectTableSql(cloum, false));
		List<Object> args = new ArrayList<>();
		List<FieldConfig> fs = bc.getFieldlist();
		valueMap = createValueMap(entiry, valueMap, fs, sql);

		createWhereSql(bc, sql, fs, args, operationMap, valueMap);
		return jdbcExecutor.queryForSimpleObject(sql.toString(), returnType, args.toArray());
	}

	@Override
	public boolean hasById(Object t, Object... ids) {

		BeanConfig bc = getConfig(t);

		Sqlbuilder countsql = new Sqlbuilder(bc.getCountTableSql());
		if (t instanceof Class<?>) {
			createIdWhere(bc, countsql);
			return jdbcExecutor.queryForSimpleObject(countsql.toString(), Integer.class, ids) > 0;
		} else {
			List<Object> args = new ArrayList<>();
			Map<String, Object> valueMap = createValueMap(t, null, bc.getIdField(), countsql.getBuilder());
			createIdWhere(bc, countsql, args, valueMap);
			for (Object id : ids) {
				args.add(id);
			}
			return jdbcExecutor.queryForSimpleObject(countsql.toString(), Integer.class, args.toArray()) > 0;
		}
	}

	@Override
	public <T> T findById(Object configId, String field, Class<T> returnType, Object... ids) {
		BeanConfig bc = getConfig(configId);
		if (bc == null) {
			throw new ConfigException("id" + configId + "没有配置");
		}
		String selectSql = bc.getSelectTableSql(field, false);
		StringBuilder sql = new StringBuilder(selectSql);
		Sqlbuilder selectsql = new Sqlbuilder(sql);
		if (configId instanceof Class<?>) {
			createIdWhere(bc, selectsql);
			return queryForObject(jdbcExecutor, selectsql.toString(), ids, bc, bc.getFieldlist(), returnType);
		} else {
			List<Object> args = new ArrayList<>();
			Map<String, Object> valueMap = createValueMap(configId, null, bc.getIdField(), sql);
			createIdWhere(bc, selectsql, args, valueMap);
			for (Object id : ids) {
				args.add(id);
			}
			return queryForObject(jdbcExecutor, selectsql.toString(), args.toArray(), bc, bc.getFieldlist(),
					returnType);
		}
	}

	@Override
	public <T> T searchFieldById(Class<?> entiry, Class<T> type, String fieldName, Object... ids) {
		BeanConfig bc = getConfig(entiry);
		FieldConfig fc = bc.getField(fieldName);
		String cloum = fc == null ? fieldName : fc.getColumn();
		Sqlbuilder countsql = new Sqlbuilder(bc.getSelectTableSql(cloum, false));
		createIdWhere(bc, countsql);
		return jdbcExecutor.queryForSimpleObject(countsql.toString(), type, ids);
	}

	@Override
	public <T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type,
			Object[] args) {
		return searchSqlForPage(sql, whereSql, page, type, args, null);
	}

	@Override
	public <T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type) {
		return searchSqlForPage(sql, whereSql, page, type, null);
	}

	@Override
	public <T> T findById(Object configId, Class<T> returnType, Object... ids) {
		return findById(configId, null, returnType, ids);
	}

	@Override
	public List<Map<String, Object>> searchForList(@NotNull Object id, String select, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isOrder) {
		List<Object> args = new ArrayList<>();
		BeanConfig bc = getConfig(id);

		String selectsql = bc.getSelectTableSql(select, false);

		StringBuilder sql = new StringBuilder(selectsql);
		valueMap = createValueMap(id, valueMap, bc.getFieldlist(), sql);
		createWhereSql(bc, sql, bc.getFieldlist(), args, operation, valueMap);
		if (isOrder) {
			createOrders(bc.getOrders(), sql, bc, valueMap);
		}
		return jdbcExecutor.queryForListMap(sql.toString(), args.toArray());
	}

	@Override
	public List<Map<String, Object>> searchForList(@NotNull Object id, String select, Map<String, Operation> operation,
			Map<String, Object> valueMap) {
		return searchForList(id, select, operation, valueMap, true);
	}

	@Override
	public List<Map<String, Object>> searchForList(@NotNull Object id, String select,
			Map<String, Operation> operation) {
		return searchForList(id, select, operation, null);
	}

	@Override
	public List<Map<String, Object>> searchForList(@NotNull Object id, String select) {
		return searchForList(id, select, null);
	}

	@Override
	public List<Map<String, Object>> searchForList(@NotNull Object id) {
		return searchForList(id, null, null, null, true);
	}

	@Override
	public LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation, Map<String, Object> valueMap, boolean isOrder) {
		List<Object> args = new ArrayList<>();
		BeanConfig bc = getConfig(id);
		StringBuilder sql = new StringBuilder(bc.getSelectTableSql(field, false));
		valueMap = createValueMap(id, valueMap, bc.getFieldlist(), sql);
		List<FieldConfig> fieldsConfigs = createJoinSql(sql, bc, valueMap, false);
		int start = sql.length();
		createWhereSql(bc, sql, bc.getFieldlist(), args, operation, valueMap);
		int groupIndex = sql.indexOf(" GROUP BY ");
		// count 和group 不能同时出现如果同时出现则需要换
		String tempCountSql = null;
		if (groupIndex != -1) {
			//TODO 此处存在异议需要截取到 select from 那块而不是直接套壳
			tempCountSql = " FROM ( " + sql + " ) as count2";
		} else {
			tempCountSql = sql.substring(start, sql.length());
		}
		return toPage(jdbcExecutor, pageExecutor, sql, tempCountSql, pageable, id, args.toArray(), bc, fieldsConfigs,
				valueMap, isOrder,groupIndex!=-1);
	}

	@Override
	public LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation, Map<String, Object> valueMap) {
		return searchForPage(id, field, pageable, operation, valueMap, true);
	}

	@Override
	public LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation) {
		return searchForPage(id, field, pageable, operation, null);
	}

	@Override
	public LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable) {
		return searchForPage(id, field, pageable, null);
	}

	@Override
	public LPage<Map<String, Object>> searchForPage(Object id, LPageable pageable) {
		return searchForPage(id, null, pageable, null);
	}

	@Override
	public <T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, LRowMapping<T> rowmapping, Class<T> returnType) {
		return searchForPage(id, null, pageable, operation, valueMap, isTransient, rowmapping, returnType);
	}

	@Override
	public <T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, Class<T> returnType) {
		return searchForPage(id, pageable, operation, valueMap, isTransient, null, returnType);
	}

	@Override
	public <T> T findOne(Object config, Map<String, Object> valueMap, Map<String, Operation> operationMap,
			boolean isTransient, Class<T> returnType) {
		List<Object> args = new ArrayList<>();
		BeanConfig bc = getConfig(config);
		String selectsql = bc.getSelectTableSql(null, isTransient);
		StringBuilder sql = new StringBuilder(selectsql);
		valueMap = createValueMap(config, valueMap, bc.getFieldlist(), sql);
		List<FieldConfig> fields = createJoinSql(sql, bc, valueMap, isTransient);
		List<FieldConfig> fieldConfigs = fields;
		if (fieldConfigs != null && fieldConfigs.size() > 0) {
			fieldConfigs.addAll(isTransient ? bc.getAllFields() : bc.getFieldlist());
		} else {
			fieldConfigs = isTransient ? bc.getAllFields() : bc.getFieldlist();
		}
		createWhereSql(bc, sql, bc.getFieldlist(), args, operationMap, valueMap);
		try {

			return jdbcExecutor.queryForObject(sql.toString(), new RowMapping<T>(bc, fieldConfigs, null),
					args.toArray());
		} catch (EmptyResultDataAccessException e) {

			return null;
		}
	}

	@Override
	public <T> T findOne(Object config, Map<String, Object> valueMap, Map<String, Operation> operationMap) {

		return findOne(config, valueMap, operationMap, false, null);
	}

	@Override
	public <T> T findOne(Object config, Map<String, Object> valueMap) {

		return findOne(config, valueMap, null);
	}

	@Override
	public <T> T findOne(Object config) {
		return findOne(config, null);
	}

	@Override
	public <T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isOrder, Class<T> returnType) {
		return searchForList(id, field, operation, valueMap, false, isOrder, null, returnType);
	}

	@Override
	public <T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, boolean isOrder, LRowMapping<T> rowmapping,
			Class<T> returnType) {
		return searchForList(id, field, operation, valueMap, isTransient, isOrder, rowmapping, null, returnType);
	}

	@Override
	public <T> List<T> searchForList(@NotNull Object id, Class<T> returnType, LSort sort) {

		return searchForList(id, null, null, null, false, false, null, sort, returnType);
	}

	@Override
	public <T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, boolean isOrder, LRowMapping<T> rowmapping, LSort sort,
			Class<T> returnType) {
		return searchForList(id, field, operation, valueMap, isTransient, isOrder, false, rowmapping, sort, returnType);
	}

	@Override
	public <T> List<T> searchForListJoin(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isNoJoin, LSort sort, Class<T> returnType) {

		return searchForList(id, field, operation, valueMap, false, true, false, null, sort, returnType);
	}

	@Override
	public <T> LPage<T> searchForPage(Object id, String field, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, boolean isOrder, Class<T> returnType) {
		return searchForPage(id, field, pageable, operation, valueMap, isTransient, isOrder, null, returnType);
	}

	@Override
	public <T> List<T> findByIds(Object configId, Class<?> returnType, Object... ids) {
		BeanConfig bc = getConfig(configId);
		if (bc == null) {
			throw new ConfigException("id" + configId + "没有配置");
		}
		String selectSql = bc.getSelectTableSql(null, false);
		StringBuilder sql = new StringBuilder(selectSql);
		Sqlbuilder selectsql = new Sqlbuilder(sql);
		String id = bc.getIdField().get(0).getColumn();
		selectsql.where(id + " in ").in(ids, true);
		return jdbcExecutor.queryForList(selectsql.toString(), new RowMapping<T>(bc, bc.getFieldlist(), null), ids);
	}

}
