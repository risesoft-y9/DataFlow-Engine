package net.risedata.jdbc.executor.search;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.mapping.LRowMapping;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.search.LPageable;
import net.risedata.jdbc.search.LSort;

/**
 * 执行查询操作类的执行器
 * 
 * @author libo 2021年2月10日
 */
public interface SearchExecutor {

	/**
	 * 查询返回map形式的list
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param select     查询的字段 当查询的字段为 null的时候则使用*
	 * @param operation  自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param valueMap   自定义的值
	 * @param isOrder    是否排序
	 * @param returnType 返回值
	 * @return
	 */
	List<Map<String, Object>> searchForList(@NotNull Object id, String select, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isOrder);

	/**
	 * 查询返回map形式的list 会默认排序
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param select     查询的字段 当查询的字段为 null的时候则使用*
	 * @param operation  自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param valueMap   自定义的值
	 * @param returnType 返回值
	 * @return
	 */
	List<Map<String, Object>> searchForList(@NotNull Object id, String select, Map<String, Operation> operation,
			Map<String, Object> valueMap);

	/**
	 * 查询返回map形式的list 会默认排序
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param select     查询的字段 当查询的字段为 null的时候则使用*
	 * @param operation  自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param returnType 返回值
	 * @return
	 */
	List<Map<String, Object>> searchForList(@NotNull Object id, String select, Map<String, Operation> operation);

	/**
	 * 查询返回map形式的list 会默认排序
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param select     查询的字段 当查询的字段为 null的时候则使用*
	 * @param returnType 返回值
	 * @return
	 */
	List<Map<String, Object>> searchForList(@NotNull Object id, String select);

	/**
	 * 查询返回map形式的list 会默认排序
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param select     查询的字段 当查询的字段为 null的时候则使用*
	 * @param returnType 返回值
	 * @return
	 */
	List<Map<String, Object>> searchForList(@NotNull Object id);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id          id 可以是 String Class Object
	 * @param field       查询的字段 当查询的字段为 null的时候则使用*
	 * @param operation   自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param valueMap    自定义的值
	 * @param isTransient 是否映射增加了@transient 的字段 的同时增加了cloum的
	 * @param isOrder     是否排序
	 * @param rowmapping  自定义的映射
	 * @param returnType  返回值
	 * @return
	 */
	<T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, boolean isOrder, LRowMapping<T> rowmapping,
			Class<T> returnType);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id          id 可以是 String Class Object
	 * @param field       查询的字段 当查询的字段为 null的时候则使用*
	 * @param operation   自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param valueMap    自定义的值
	 * @param isTransient 是否映射增加了@transient 的字段 的同时增加了cloum的
	 * @param isOrder     是否排序
	 * @param rowmapping  自定义的映射
	 * @param sort        排序
	 * @param returnType  返回值
	 * 
	 * @return
	 */
	<T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, boolean isOrder, LRowMapping<T> rowmapping, LSort sort,
			Class<T> returnType);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id          id 可以是 String Class Object
	 * @param field       查询的字段 当查询的字段为 null的时候则使用*
	 * @param operation   自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param valueMap    自定义的值
	 * @param isTransient 是否映射增加了@transient 的字段 的同时增加了cloum的
	 * @param isOrder     是否排序
	 * @param isNoJoin    是否不连接
	 * @param rowmapping  自定义的映射
	 * @param sort        排序
	 * @param returnType  返回值
	 * 
	 * @return
	 */
	<T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, boolean isOrder, boolean isNoJoin,
			LRowMapping<T> rowmapping, LSort sort, Class<T> returnType);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param field      查询的字段 当查询的字段为 null的时候则使用*
	 * @param operation  自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param valueMap   自定义的值
	 * @param isNoJoin   是否不连接
	 * @param rowmapping 自定义的映射
	 * @param sort       排序
	 * @param returnType 返回值
	 * 
	 * @return
	 */
	<T> List<T> searchForListJoin(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isNoJoin, LSort sort, Class<T> returnType);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param sort       排序
	 * @param returnType 返回值
	 * 
	 * @return
	 */
	<T> List<T> searchForList(@NotNull Object id, Class<T> returnType, LSort sort);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param field      查询的字段 当查询的字段为 null的时候则使用*
	 * @param operation  自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param valueMap   自定义的值
	 * @param isOrder    是否排序
	 * @param returnType 返回值
	 * @return
	 */
	<T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isOrder, Class<T> returnType);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id          id 可以是 String Class Object
	 * @param field       查询的字段 当查询的字段为 null的时候则使用*
	 * @param operation   自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param valueMap    自定义的值
	 * @param isTransient 是否映射增加了@transient 的字段 的同时增加了cloum的
	 * @param rowmapping  自定义的映射
	 * @param returnType  返回值
	 * @return
	 */
	<T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, LRowMapping<T> rowmapping, Class<T> returnType);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param field      查询的字段 当查询的字段为 null的时候则使用*
	 * @param operation  自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param valueMap   自定义的值
	 * @param rowmapping 自定义的映射
	 * @param returnType 返回值
	 * @return
	 */
	<T> List<T> searchForList(@NotNull Object id, String field, Map<String, Operation> operation,
			Map<String, Object> valueMap, LRowMapping<T> rowmapping, Class<T> returnType);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param operation  自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param valueMap   自定义的值
	 * @param rowmapping 自定义的映射
	 * @param returnType 返回值
	 * @return
	 */
	<T> List<T> searchForList(@NotNull Object id, Map<String, Operation> operation, Map<String, Object> valueMap,
			LRowMapping<T> rowmapping, Class<T> returnType);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param operation  自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param valueMap   自定义的值
	 * @param returnType 返回值
	 * @return
	 */
	<T> List<T> searchForList(@NotNull Object id, Map<String, Operation> operation, Map<String, Object> valueMap,
			Class<T> returnType);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param operation  自定义的操作 使用 字段名 ==> 自自定义的操作
	 * @param returnType 返回值
	 * @return
	 */
	<T> List<T> searchForList(@NotNull Object id, Map<String, Operation> operation, Class<T> returnType);

	/**
	 * 查询返回list
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param returnType 返回值
	 * @return
	 */
	<T> List<T> searchForList(@NotNull Object id, Class<T> returnType);

	/**
	 * 查询并且分页返回形式为map
	 * 
	 * @param <T>
	 * @param id        id 可以是 String Class Object
	 * @param field     查询的字段
	 * @param pageable  分页信息
	 * @param operation 自定义的操作
	 * @param valueMap  自定义的值
	 * @param isOrder   是否排序
	 * @return
	 */
	LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation, Map<String, Object> valueMap, boolean isOrder);

	/**
	 * 查询并且分页返回形式为map 默认排序
	 * 
	 * @param <T>
	 * @param id        id 可以是 String Class Object
	 * @param field     查询的字段
	 * @param pageable  分页信息
	 * @param operation 自定义的操作
	 * @param valueMap  自定义的值
	 * @return
	 */
	LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation, Map<String, Object> valueMap);

	/**
	 * 查询并且分页返回形式为map 默认排序
	 * 
	 * @param <T>
	 * @param id        id 可以是 String Class Object
	 * @param field     查询的字段
	 * @param pageable  分页信息
	 * @param operation 自定义的操作
	 * @return
	 */
	LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable,
			Map<String, Operation> operation);

	/**
	 * 查询并且分页返回形式为map 默认排序
	 * 
	 * @param <T>
	 * @param id       id 可以是 String Class Object
	 * @param field    查询的字段
	 * @param pageable 分页信息
	 * @return
	 */
	LPage<Map<String, Object>> searchForPage(Object id, String field, LPageable pageable);

	/**
	 * 查询并且分页返回形式为map 默认排序
	 * 
	 * @param <T>
	 * @param id       id 可以是 String Class Object
	 * @param pageable 分页信息
	 * @return
	 */
	LPage<Map<String, Object>> searchForPage(Object id, LPageable pageable);

	/**
	 * 查询并且分页
	 * 
	 * @param <T>
	 * @param id          id 可以是 String Class Object
	 * @param field       查询的字段
	 * @param pageable    分页信息
	 * @param operation   自定义的操作
	 * @param valueMap    自定义的值
	 * @param isTransient 是否映射增加了@transient 的字段 的同时增加了cloum的
	 * @param isOrder     是否排序
	 * @param rowmapping  自定义映射
	 * @param returnType  返回值
	 * @return
	 */
	<T> LPage<T> searchForPage(Object id, String field, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, boolean isOrder, LRowMapping<T> rowmapping,
			Class<T> returnType);

	/**
	 * 查询并且分页
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param field      查询的字段
	 * @param pageable   分页信息
	 * @param operation  自定义的操作
	 * @param valueMap   自定义的值
	 * @param rowmapping 自定义映射
	 * @param returnType 返回值
	 * @return
	 */
	<T> LPage<T> searchForPage(Object id, String field, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, boolean isOrder, Class<T> returnType);

	/**
	 * 查询并且分页
	 * 
	 * @param <T>
	 * @param id          id 可以是 String Class Object
	 * @param field       查询的字段
	 * @param pageable    分页信息
	 * @param operation   自定义的操作
	 * @param valueMap    自定义的值
	 * @param isTransient 是否映射增加了@transient 的字段 的同时增加了cloum的
	 * @param rowmapping  自定义映射
	 * @param returnType  返回值
	 * @return
	 */
	<T> LPage<T> searchForPage(Object id, String field, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, LRowMapping<T> rowmapping, Class<T> returnType);

	/**
	 * 查询并且分页
	 * 
	 * @param <T>
	 * @param id          id 可以是 String Class Object
	 * @param pageable    分页信息
	 * @param operation   自定义的操作
	 * @param valueMap    自定义的值
	 * @param isTransient 是否映射增加了@transient 的字段 的同时增加了cloum的
	 * @param rowmapping  自定义映射
	 * @param returnType  返回值
	 * @return
	 */
	<T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, LRowMapping<T> rowmapping, Class<T> returnType);

	/**
	 * 查询并且分页
	 * 
	 * @param <T>
	 * @param id          id 可以是 String Class Object
	 * @param pageable    分页信息
	 * @param operation   自定义的操作
	 * @param valueMap    自定义的值
	 * @param isTransient 是否映射增加了@transient 的字段 的同时增加了cloum的
	 * @param rowmapping  自定义映射
	 * @param returnType  返回值
	 * @return
	 */
	<T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, boolean isTransient, Class<T> returnType);

	/**
	 * 查询并且分页
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param field      查询的字段
	 * @param pageable   分页信息
	 * @param operation  自定义的操作
	 * @param valueMap   自定义的值
	 * @param rowmapping 自定义映射
	 * @param returnType 返回值
	 * @return
	 */
	<T> LPage<T> searchForPage(Object id, String field, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, LRowMapping<T> rowmapping, Class<T> returnType);

	/**
	 * 查询并且分页
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param pageable   分页信息
	 * @param operation  自定义的操作
	 * @param valueMap   自定义的值
	 * @param rowmapping 自定义映射
	 * @param returnType 返回值
	 * @return
	 */
	<T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, LRowMapping<T> rowmapping, Class<T> returnType);

	/**
	 * 查询并且分页
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param pageable   分页信息
	 * @param operation  自定义的操作
	 * @param valueMap   自定义的值
	 * @param returnType 返回值
	 * @return
	 */
	<T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation,
			Map<String, Object> valueMap, Class<T> returnType);

	/**
	 * 查询并且分页
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param pageable   分页信息
	 * @param operation  自定义的操作
	 * @param returnType 返回值
	 * @return
	 */
	<T> LPage<T> searchForPage(Object id, LPageable pageable, Map<String, Operation> operation, Class<T> returnType);

	/**
	 * 查询并且分页
	 * 
	 * @param <T>
	 * @param id         id 可以是 String Class Object
	 * @param pageable   分页信息
	 * @param returnType 返回值
	 * @return
	 */
	<T> LPage<T> searchForPage(Object id, LPageable pageable, Class<T> returnType);

	/**
	 * 根据传入的sql进行 查询分页的映射
	 * 
	 * @param <T>
	 * @param sql         查询的sql
	 * @param whereSql    条件sql
	 * @param page        分页对象
	 * @param type        类型
	 * @param args        参数
	 * @param isTransient 是否映射增加了@transient 的字段 的同时增加了cloum的
	 * @param rowMapping  自定义的映射
	 * @return
	 */
	<T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type, Object[] args,
			boolean isTransient, LRowMapping<T> rowMapping);

	/**
	 * 根据传入的sql进行 查询分页的映射
	 * 
	 * @param <T>
	 * @param sql        查询的sql
	 * @param whereSql   条件sql
	 * @param page       分页对象
	 * @param type       类型
	 * @param args       参数
	 * @param rowMapping 自定义的映射
	 * @return
	 */
	<T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type, Object[] args,
			LRowMapping<T> rowMapping);

	/**
	 * 根据传入的sql进行 查询分页的映射
	 * 
	 * @param <T>
	 * @param sql      查询的sql
	 * @param whereSql 条件sql
	 * @param page     分页对象
	 * @param type     类型
	 * @param args     参数
	 * @return
	 */
	<T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type, Object[] args);

	/**
	 * 根据传入的sql进行 查询分页的映射
	 * 
	 * @param <T>
	 * @param sql      查询的sql
	 * @param whereSql 条件sql
	 * @param page     分页对象
	 * @param type     类型
	 * @return
	 */
	<T> LPage<T> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Class<T> type);

	/**
	 * 拿到一个字段的配置
	 * 
	 * @param <T>
	 * @param entiry
	 * @param field
	 * @param returnType
	 * @param operationMap
	 * @param valueMap
	 * @return
	 */
	<T> T searchField(Object entiry, String field, Class<T> returnType, Map<String, Operation> operationMap,
			Map<String, Object> valueMap);

	/**
	 * 根据id集合 查看是否存在 注意 ids的顺序必须和entiry里面的顺序一一对应 注意 如果传入的 configid 有值则会先添加到args中
	 * 随后添加ids的值
	 * 
	 * @param t
	 * @param id
	 * @return
	 */
	boolean hasById(Object t, Object... ids);

	/**
	 * 根据id拿到对象 注意 如果传入的 configid 有值则会先添加到args中 随后添加ids的值
	 * 
	 * @param <T>
	 * @param configId
	 * @param field
	 * @param returnType
	 * @param ids
	 * @return
	 */
	<T> T findById(Object configId, String field, Class<T> returnType, Object... ids);

	/**
	 * 根据查找的条件拿到单个对象
	 * 
	 * @param <T>
	 * @param config       id
	 * @param valueMap     值map
	 * @param operationMap 操作map
	 * @param isTransient  是否拿到添加了transient注解的字段
	 * @param returnType   返回类型
	 * @return
	 */
	<T> T findOne(Object config, Map<String, Object> valueMap, Map<String, Operation> operationMap, boolean isTransient,
			Class<T> returnType);

	/**
	 * 根据查找的条件拿到单个对象
	 * 
	 * @param <T>
	 * @param config       id
	 * @param valueMap     值map
	 * @param operationMap 操作map
	 * @return
	 */
	<T> T findOne(Object config, Map<String, Object> valueMap, Map<String, Operation> operationMap);

	/**
	 * 根据查找的条件拿到单个对象
	 * 
	 * @param <T>
	 * @param config       id
	 * @param valueMap     值map
	 * @param operationMap 操作map
	 * @return
	 */
	<T> T findOne(Object config, Map<String, Object> valueMap);

	/**
	 * 根据查找的条件拿到单个对象
	 * 
	 * @param <T>
	 * @param config       id
	 * @param valueMap     值map
	 * @param operationMap 操作map
	 * @return
	 */
	<T> T findOne(Object config);

	/**
	 * 根据id拿到对象 注意 如果传入的 configid 有值则会先添加到args中 随后添加ids的值
	 * 
	 * @param <T>
	 * @param configId
	 * @param returnType
	 * @param ids
	 * @return
	 */
	<T> T findById(Object configId, Class<T> returnType, Object... ids);

	/**
	 * 查询单个字段根据id查询
	 * 
	 * @param <T>
	 * @param entiry
	 * @param type
	 * @param fieldName
	 * @param ids
	 * @return
	 */
	<T> T searchFieldById(Class<?> entiry, Class<T> type, String fieldName, Object... ids);

	LPage<Map<String, Object>> searchSqlForPage(StringBuilder sql, String whereSql, LPageable page, Object[] args);

	<T> List<T> findByIds(Object t, Class<?> t2, Object... ids);

}
