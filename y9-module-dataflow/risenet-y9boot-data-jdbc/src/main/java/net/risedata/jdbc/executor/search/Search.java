package net.risedata.jdbc.executor.search;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.mapping.LRowMapping;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.search.LPageable;

/**
 * 提供静态的search功能</br>
 * 请使用 {@link SearchExecutor}此类以过时
 * 
 * @author libo 2021年5月24日
 */
public class Search {

	private static SearchExecutor SEARCH;

	public static void setSearchExecutor(SearchExecutor search) {
		SEARCH = search;
	}

	public static List<Map<String, Object>> searchForList(@NotNull Object id, String select,
			Map<String, Operation> operation, Map<String, Object> valueMap, boolean isOrder) {
		return SEARCH.searchForList(id, select, operation, valueMap, isOrder);
	}

	public static List<Map<String, Object>> searchForList(@NotNull Object id, String select,
			Map<String, Operation> operation, Map<String, Object> valueMap) {
		return SEARCH.searchForList(id, select, operation, valueMap);
	}

	public static List<Map<String, Object>> searchForList(@NotNull Object id, String select,
			Map<String, Operation> operation) {
		return SEARCH.searchForList(id, select, operation);
	}

	public static List<Map<String, Object>> searchForList(@NotNull Object id, String select) {
		return SEARCH.searchForList(id, select);
	}

	public List<Map<String, Object>> searchForList(@NotNull Object id) {
		return SEARCH.searchForList(id);
	}

	public static <T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, boolean isOrder, LRowMapping<T> rowmapping,
			Class<T> returnType) {
		return SEARCH.searchForList(id, field, operation, valueMap, isTransient, isOrder, rowmapping, returnType);
	}

	public static <T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, LRowMapping<T> rowmapping, Class<T> returnType) {
		return SEARCH.searchForList(id, field, operation, valueMap, isTransient, rowmapping, returnType);
	}

	public static <T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, LRowMapping<T> rowmapping, Class<T> returnType) {
		return SEARCH.searchForList(id, field, operation, valueMap, rowmapping, returnType);
	}

	public static <T> List<T> searchForList(@NotNull Object id, Map<String, Operation> operation,
			Map<String, Object> valueMap, LRowMapping<T> rowmapping, Class<T> returnType) {
		return SEARCH.searchForList(id, operation, valueMap, rowmapping, returnType);
	}

	public static <T> List<T> searchForList(@NotNull Object id, Map<String, Operation> operation,
			Map<String, Object> valueMap, Class<T> returnType) {
		return SEARCH.searchForList(id, operation, valueMap, returnType);
	}

	public static <T> List<T> searchForList(@NotNull Object id, Map<String, Operation> operation, Class<T> returnType) {
		return SEARCH.searchForList(id, operation, returnType);
	}

	public static <T> List<T> searchForList(@NotNull Object id, Class<T> returnType) {
		return SEARCH.searchForList(id, returnType);
	}

	public static LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation, Map<String, Object> valueMap, boolean isOrder) {

		return SEARCH.searchForPage(id, field, pageable, operation, valueMap, isOrder);
	}

	public static LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation, Map<String, Object> valueMap) {
		return SEARCH.searchForPage(id, field, pageable, operation, valueMap);
	}

	public static LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation) {
		return SEARCH.searchForPage(id, field, pageable, operation);
	}

	public static LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable) {
		return SEARCH.searchForPage(id, field, pageable);
	}

	public static LPage<Map<String, Object>> searchForPage(Object id, LPageable pageable) {
		return SEARCH.searchForPage(id, pageable);
	}

	public static <T> LPage<T> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation, Map<String, Object> valueMap, boolean isTransient, boolean isOrder,
			LRowMapping<T> rowmapping, Class<T> returnType) {
		return SEARCH.searchForPage(id, field, pageable, operation, valueMap, isTransient, isOrder, rowmapping,
				returnType);
	}

	public static <T> LPage<T> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation, Map<String, Object> valueMap, boolean isTransient,
			LRowMapping<T> rowmapping, Class<T> returnType) {
		return SEARCH.searchForPage(id, field, pageable, operation, valueMap, isTransient, rowmapping, returnType);
	}

	public static <T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, LRowMapping<T> rowmapping, Class<T> returnType) {

		return SEARCH.searchForPage(id, pageable, operation, valueMap, isTransient, rowmapping, returnType);
	}

	public static <T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, Class<T> returnType) {
		return SEARCH.searchForPage(id, pageable, operation, valueMap, isTransient, returnType);
	}

	public static <T> LPage<T> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation, Map<String, Object> valueMap, LRowMapping<T> rowmapping,
			Class<T> returnType) {
		return SEARCH.searchForPage(id, pageable, operation, valueMap, rowmapping, returnType);
	}

	public static <T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, LRowMapping<T> rowmapping, Class<T> returnType) {
		return SEARCH.searchForPage(id, pageable, operation, valueMap, rowmapping, returnType);
	}

	public static <T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, Class<T> returnType) {
		return SEARCH.searchForPage(id, pageable, operation, valueMap, returnType);
	}

	public static <T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Class<T> returnType) {
		return SEARCH.searchForPage(id, pageable, operation, returnType);
	}

	public static <T> LPage<T> searchForPage(Object id, LPageable pageable, Class<T> returnType) {
		return SEARCH.searchForPage(id, pageable, returnType);
	}

	public static <T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type,
			Object[] args, boolean isTransient, LRowMapping<T> rowMapping) {
		return SEARCH.searchSqlForPage(sql, whereSql, page, type, args, isTransient, rowMapping);
	}

	public static <T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type,
			Object[] args, LRowMapping<T> rowMapping) {
		return SEARCH.searchSqlForPage(sql, whereSql, page, type, args, rowMapping);
	}

	public static <T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type,
			Object[] args) {
		return SEARCH.searchSqlForPage(sql, whereSql, page, type, args);
	}

	public static <T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type) {
		return SEARCH.searchSqlForPage(sql, whereSql, page, type);
	}

	public static <T> T searchField(Object entiry, String field, Class<T> returnType,
			Map<String, Operation> operationMap, Map<String, Object> valueMap) {
		return SEARCH.searchField(entiry, field, returnType, operationMap, valueMap);
	}

	public static boolean hasById(Object t, Object... ids) {

		return SEARCH.hasById(t, ids);
	}

	public static <T> T findById(Object configId, String field, Class<T> returnType, Object... ids) {
		return SEARCH.findById(configId, field, returnType, ids);
	}

	public static <T> T findById(Object configId, Class<T> returnType, Object... ids) {
		return SEARCH.findById(configId, returnType, ids);
	}

	public static <T> T searchFieldById(Class<?> entiry, Class<T> type, String fieldName, Object... ids) {
		return SEARCH.searchFieldById(entiry, type, fieldName, ids);
	}

	public static LPage<Map<String, Object>> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page,
			Object[] args) {
		return SEARCH.searchSqlForPage(sql, whereSql, page, args);
	}

}
