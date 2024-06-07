package net.risedata.jdbc.executor.jdbc;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

/**
 * 执行底层数据库操作的jdbc执行类
 * 
 * @author libo 2021年2月7日
 */
public interface JdbcExecutor {
	/**
	 * 此方法会执行sql 设置好args 并且返回 对应的类型类型根据RowMapper的类型返回
	 * 
	 * @param <T>
	 * @param sql
	 * @param args
	 * @param rowMapper
	 * @return
	 */
	<T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args);

	/**
	 * 执行返回对象 注意这个类型会根据beanconfig判断是存在映射
	 * 
	 * @param <T>
	 * @param sql
	 * @param args
	 * @param returnType
	 * @return
	 */
	<T> T queryForObject(String sql, Class<T> returnType, Object... args);

	/**
	 * 此方法会执行sql 设置好args 并且返回 对应的类型 注意: 返回的 类型只包含一些简单的类型
	 * 
	 * @param <T>
	 * @param sql
	 * @param args
	 * @param requiredType
	 * @return
	 */
	<T> T queryForSimpleObject(String sql, Class<T> requiredType, Object... args);

	/**
	 * 执行sql 并且返回list 泛型为简单的一些类型
	 * 
	 * @param <T>
	 * @param sql
	 * @param args
	 * @param elementType
	 * @return
	 */
	<T> List<T> queryForSimpleList(String sql, Class<T> elementType, Object... args);

	/**
	 * 执行sql 返回lsit 映射根据config中的映射
	 * 
	 * @param <T>
	 * @param sql
	 * @param args
	 * @param elementType
	 * @return
	 */
	<T> List<T> queryForList(String sql, Class<T> elementType, Object... args);

	/**
	 * 执行sql 返回lsit 映射根据config中的映射
	 * 
	 * @param <T>
	 * @param sql
	 * 
	 * @param elementType
	 * @return
	 */
	<T> List<T> queryForList(String sql, Class<T> elementType);

	/**
	 * 执行sql 根据传入的映射返回对应映射的list
	 * 
	 * @param <T>
	 * @param sql
	 * @param args
	 * @param rowMapper
	 * @return
	 */
	<T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args);

	/**
	 * update
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	Integer update(String sql, Object... args);
	/**
	 * update
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	Integer update(String sql);

	int[] batchUpdate(String sql, List<Object[]> batchArgs);

	/**
	 * 查询返回map
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	Map<String, Object> queryForMap(String sql, Object... args);

	/**
	 * 查询返回map
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	List<Map<String, Object>> queryForListMap(String sql, Object... args);

	Map<String, Object> queryForMap(String sql);

	<T> T queryForObject(String sql, Class<T> returnType);

	int[] batchUpdate(String[] array);

}
