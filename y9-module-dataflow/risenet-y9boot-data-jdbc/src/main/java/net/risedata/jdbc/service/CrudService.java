package net.risedata.jdbc.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.executor.delete.DeleteExecutor;
import net.risedata.jdbc.executor.insert.InsertExecutor;
import net.risedata.jdbc.executor.search.SearchExecutor;
import net.risedata.jdbc.executor.update.UpdateExecutor;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.search.LPageable;

public interface CrudService<T, ID> {

	/**
	 * 实现该类的子类需返还执行查询的执行器
	 * 
	 * @return
	 */
	SearchExecutor getSearchExecutor();

	/**
	 * 实现该类的子类需返还执行插入的执行器
	 * 
	 * @return
	 */
	InsertExecutor getInsertExecutor();

	/**
	 * 实现该类的子类需返还执行删除的执行器
	 * 
	 * @return
	 */
	DeleteExecutor getDeleteExecutor();

	/**
	 * 实现该类的子类需返还执行删除的执行器
	 * 
	 * @return
	 */
	UpdateExecutor getUpdateExecutor();

	public Class<T> getT();

	public int deleteById(@SuppressWarnings("unchecked") ID... ids);

	/**
	 * 保存一个 e
	 * 
	 * @param t
	 */
	int save(T t);

	T findById(@SuppressWarnings("unchecked") ID... id);

	/**
	 * 根据id批量获取
	 * 
	 * @param id
	 * @return
	 */
	List<T> findByIds(@SuppressWarnings("unchecked") ID... id);

	/**
	 * 通过id删除
	 * 
	 * @param id
	 */
	int deleteById(ID id);

	/**
	 * 通过id删除
	 * 
	 * @param id
	 */
	int deleteByIds(@SuppressWarnings("unchecked") ID... ids);

	/**
	 * 根据传入的对象进行删除
	 * 
	 * @param t
	 * @return
	 */
	int delete(T t);

	/**
	 * 根据传入的对象进行删除
	 * 
	 * @param t
	 * @return
	 */
	int delete(T t, Map<String, Operation> operation);

	/**
	 * 
	 * @param t
	 * @param values
	 * @param operation
	 * @return
	 */
	int delete(T t, Map<String, Object> values, Map<String, Operation> operation);

	/**
	 * 根据id查看是否存在
	 * 
	 * @param id
	 * @return
	 */
	boolean hasById(ID id);

	/**
	 * 拿到一个entiry的一个字段并且映射成为简单的类型
	 * 
	 * @param <E>
	 * @param field
	 * @param class1
	 * @param id
	 * @return
	 */
	<E> E searchFieldById(String field, Class<E> class1, ID id);

	/**
	 * 查找
	 * 
	 * @param <E>
	 * @param entiry       实体
	 * @param field        自定义字段
	 * @param map
	 * @param operationMap
	 * @return
	 */
	List<T> search(String field, Map<String, Object> map, Map<String, Operation> operationMap, boolean isOrder);

	/**
	 * 查找
	 * 
	 * @param <E>
	 * @param entiry       实体
	 * @param field        自定义字段
	 * @param map
	 * @param operationMap
	 * @return
	 */
	List<T> search(String field, Map<String, Object> map, Map<String, Operation> operationMap);

	/**
	 * 根据id获取
	 * 
	 * @param id
	 * @return
	 */
	T getOne(ID id);

	/**
	 * 根据传入的对象获得
	 * 
	 * @param t
	 * @return
	 */
	T findOne(T t);

	T findOne(T t, Map<String, Object> valueMap, Map<String, Operation> operationMap);

	T findOne(T t, Map<String, Object> valueMap, Map<String, Operation> operationMap, boolean isTransient);

	/**
	 * 单表查询
	 * 
	 * @param entiry       查询的实体类
	 * @param valueMap     格外指定的value
	 * @param operationMap 格外指定的操作
	 * @return
	 */
	List<T> search(T entiry, Map<String, Object> valueMap, Map<String, Operation> operationMap);

	/**
	 * 单表查询
	 * 
	 * @param entiry   查询的entiry
	 * @param valueMap 格外的value
	 * @return
	 */
	List<T> search(T entiry, Map<String, Object> valueMap);

	/**
	 * 查询全部
	 * 
	 * @return
	 */
	List<T> searchAll();

	/**
	 * 单表查询
	 * 
	 * @param valueMap 值value
	 * @return
	 */
	List<T> search(Map<String, Object> valueMap);

	/**
	 * 单表查询
	 * 
	 * @param entiry 查询的entiry
	 * @return
	 */
	List<T> search(T entiry);

	/**
	 * 分页单表查询
	 * 
	 * @param entiry       查询的实体类
	 * @param page         page对象
	 * @param valueMap     格外指定的map
	 * @param operationMap 格外指定的map
	 * @return
	 */
	LPage<T> searchForPage(T entiry, LPageable page, Map<String, Object> valueMap, Map<String, Operation> operationMap);

	/**
	 * 查询所有 字段 @ transient
	 * 
	 * @param entiry
	 * @param page
	 * @param valueMap
	 * @param operationMap
	 * @return
	 */
	LPage<T> searchAllForPage(T entiry, LPageable page, Map<String, Object> valueMap,
			Map<String, Operation> operationMap);

	/**
	 * 分页单表查询
	 * 
	 * @param entiry   查询的实体类
	 * @param page     page对象
	 * @param valueMap 格外指定的map
	 * @return
	 */
	LPage<T> searchForPage(T entiry, LPageable page, Map<String, Object> valueMap);

	/**
	 * 分页单表查询
	 * 
	 * @param entiry   查询的实体类
	 * @param page     page对象
	 * @param valueMap 格外指定的map
	 * @return
	 */
	LPage<T> searchForPage(LPageable page);

	/**
	 * 根据page查询
	 * 
	 * @param page
	 * @param valueMap 类的字段放在此map中
	 * @return
	 */
	LPage<T> searchForPage(LPageable page, Map<String, Object> valueMap);

	/**
	 * 查询 根据传入的map作为参数
	 * 
	 * @param page
	 * @param valueMap
	 * @return
	 */
	LPage<T> searchAllForPage(LPageable page, Map<String, Object> valueMap);

	/**
	 * 查询 根据传入的map作为参数
	 * 
	 * @param page
	 * @param valueMap
	 * @return
	 */
	LPage<T> searchAllForPage(LPageable page, Map<String, Object> valueMap, Map<String, Operation> omap);

	/**
	 * 查询返回集合根据select 来决定
	 * 
	 * @param t
	 * @param select
	 * @return
	 */
	List<Map<String, Object>> searchForList(T t, String select);

	/**
	 * 单表查询返回select 中的字段
	 * 
	 * @param t
	 * @param select
	 * @param operationMap
	 * @return
	 */
	List<Map<String, Object>> searchForList(T t, String select, Map<String, Operation> operationMap);

	/**
	 * 分页单表查询
	 * 
	 * @param entiry   查询的实体类
	 * @param page     page对象
	 * @param valueMap 格外指定的map
	 * @return
	 */
	LPage<T> searchForPage(T entiry, LPageable page);

	/**
	 * 插入
	 * 
	 * @param entiry
	 * @return
	 */
	int insert(T entiry);

	/**
	 * 新增
	 * 
	 * @param entirys
	 * @return
	 */
	int batchInsert(Collection<T> entirys);

	/**
	 * 根据id 修改
	 * 
	 * @param entiry
	 * @return
	 */
	int updateById(T entiry);

	/**
	 * 根据id删除
	 * 
	 * @param entiry
	 * @param valueMap  格外的value
	 * @param operation where操作
	 * @return
	 */
	int updateById(T entiry, Map<String, Object> valueMap, Map<String, Operation> operation);

	/**
	 * 根据id删除
	 * 
	 * @param entiry
	 * @param operation 格外的操作
	 * @return
	 */
	int updateById(T entiry, Map<String, Operation> operation);

	/**
	 * 修改 自己指定哪些字段用于wehre 剩下不为null的全是set字段
	 * 
	 * @param entiry
	 * @param wheres
	 * @return
	 */
	int update(T entiry, List<String> wheres);
}
