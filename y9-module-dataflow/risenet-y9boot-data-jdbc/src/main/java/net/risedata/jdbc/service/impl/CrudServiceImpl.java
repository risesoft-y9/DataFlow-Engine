package net.risedata.jdbc.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.executor.delete.DeleteExecutor;
import net.risedata.jdbc.executor.insert.InsertExecutor;
import net.risedata.jdbc.executor.search.SearchExecutor;
import net.risedata.jdbc.executor.update.UpdateExecutor;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.search.LPageable;
import net.risedata.jdbc.service.CrudService;
import net.risedata.jdbc.utils.ClassUtils;

public abstract class CrudServiceImpl<T, ID> implements CrudService<T, ID> {

	/**
	 * 实现该类的子类需返还执行查询的执行器
	 * 
	 * @return
	 */
	public abstract SearchExecutor getSearchExecutor();

	/**
	 * 实现该类的子类需返还执行插入的执行器
	 * 
	 * @return
	 */
	public abstract InsertExecutor getInsertExecutor();

	/**
	 * 实现该类的子类需返还执行删除的执行器
	 * 
	 * @return
	 */
	public abstract DeleteExecutor getDeleteExecutor();

	/**
	 * 实现该类的子类需返还执行删除的执行器
	 * 
	 * @return
	 */
	public abstract UpdateExecutor getUpdateExecutor();

	Class<T> T;

	CrudServiceImpl() {
		this.T = ClassUtils.getT(this);
		//Load.loadBean(T);
	}

	@Override
	@Transactional
	public int delete(T t) {
		return getDeleteExecutor().delete(t);
	}

	@Override
	@Transactional
	public int deleteByIds(@SuppressWarnings("unchecked") ID... ids) {
		return getDeleteExecutor().deleteByIds(T, ids);
	}

	@Override
	@Transactional
	public int deleteById(@SuppressWarnings("unchecked") ID... ids) {
		return getDeleteExecutor().deleteById(T, ids);
	}
	
	@Override
	public List<T> search(Map<String, Object> valueMap) {
		return getSearchExecutor().searchForList(T, null, valueMap, T);
	}

	@Override
	@Transactional
	public int delete(T t, Map<String, Object> values, Map<String, Operation> operation) {

		return getDeleteExecutor().delete(t, values, operation);
	}

	@Override
	public T findById(@SuppressWarnings("unchecked") ID... id) {

		return getSearchExecutor().findById(T, T, id);
	}

	@Override
	@Transactional
	public int delete(T t, Map<String, Operation> operation) {
		return getDeleteExecutor().delete(t, null, operation);
	}

	@Override
	public T findOne(T t) {
		return findOne(t, null, null);
	}

	@Override
	public T findOne(T t, Map<String, Object> valueMap, Map<String, Operation> operationMap) {
		return findOne(t, valueMap, operationMap, false);
	}

	@Override
	public T findOne(T t, Map<String, Object> valueMap, Map<String, Operation> operationMap, boolean isTransient) {
		return getSearchExecutor().findOne(t, valueMap, operationMap, isTransient, T);
	}

	@Override
	@Transactional
	public int save(T t) {
		if (getSearchExecutor().hasById(t)) {
			return getUpdateExecutor().updateById(t);
		} else {
			return getInsertExecutor().insert(t);
		}
	}

	@Override
	@Transactional
	public int deleteById(ID id) {
		return getDeleteExecutor().deleteById(T, id);
	}

	@Override
	public boolean hasById(ID id) {
		return getSearchExecutor().hasById(T, id);
	}

	@Override
	public <E> E searchFieldById(String field, Class<E> class1, ID id) {
		return getSearchExecutor().searchFieldById(T, class1, field, id);
	}

	@Override
	public T getOne(ID id) {
		return getSearchExecutor().findById(T, T, id);
	}

	@Override
	public List<T> search(T entiry, Map<String, Object> valueMap, Map<String, Operation> operationMap) {
		return getSearchExecutor().searchForList(entiry, operationMap, valueMap, T);
	}

	@Override
	public List<T> search(T entiry, Map<String, Object> valueMap) {
		return getSearchExecutor().searchForList(entiry, null, valueMap, T);
	}

	@Override
	public List<T> search(T entiry) {
		return getSearchExecutor().searchForList(entiry, T);
	}

	@Override
	public LPage<T> searchForPage(T entiry, LPageable page, Map<String, Object> valueMap,
			Map<String, Operation> operationMap) {
		return getSearchExecutor().searchForPage(entiry, page, operationMap, valueMap, T);
	}

	@Override
	public LPage<T> searchAllForPage(T entiry, LPageable page, Map<String, Object> valueMap,
			Map<String, Operation> operationMap) {
		return getSearchExecutor().searchForPage(entiry, page, operationMap, valueMap, true, T);
	}

	@Override
	public LPage<T> searchForPage(T entiry, LPageable page, Map<String, Object> valueMap) {
		return getSearchExecutor().searchForPage(entiry, page, null, valueMap, T);
	}

	@Override
	public LPage<T> searchForPage(LPageable page) {
		return getSearchExecutor().searchForPage(T, page, T);
	}

	@Override
	public LPage<T> searchForPage(LPageable page, Map<String, Object> valueMap) {
		return searchForPage(null, page, valueMap, null);
	}

	@Override
	public LPage<T> searchAllForPage(LPageable page, Map<String, Object> valueMap) {
		return getSearchExecutor().searchForPage(T, null, page, null, valueMap, true, null, T);
	}

	@Override
	public LPage<T> searchAllForPage(LPageable page, Map<String, Object> valueMap, Map<String, Operation> omap) {
		return getSearchExecutor().searchForPage(T, null, page, omap, valueMap, true, null, T);
	}

	@Override
	public List<Map<String, Object>> searchForList(T t, String select) {
		return getSearchExecutor().searchForList(t, select);
	}

	@Override
	public List<Map<String, Object>> searchForList(T t, String select, Map<String, Operation> operationMap) {
		return getSearchExecutor().searchForList(t, select, operationMap);
	}

	@Override
	public LPage<T> searchForPage(T entiry, LPageable page) {
		return getSearchExecutor().searchForPage(entiry, page, T);
	}

	@Transactional
	@Override
	public int insert(T entiry) {
		return getInsertExecutor().insert(entiry);
	}

	@Transactional
	@Override
	public int batchInsert(Collection<T> entirys) {
		int[] res = getInsertExecutor().batchInsert(entirys);
		int count = 0;
		for (int i : res) {
			count += i;
		}
		return count;
	}

	@Transactional
	@Override
	public int updateById(T entiry) {
		return getUpdateExecutor().updateById(entiry);
	}

	@Transactional
	@Override
	public int updateById(T entiry, Map<String, Object> valueMap, Map<String, Operation> operation) {
		return getUpdateExecutor().updateById(entiry, valueMap, operation);
	}

	@Transactional
	@Override
	public int updateById(T entiry, Map<String, Operation> operation) {
		return getUpdateExecutor().updateById(entiry, null, operation);
	}

	@Transactional
	@Override
	public int update(T entiry, List<String> wheres) {
		return getUpdateExecutor().update(entiry, wheres);
	}

	@Override
	public List<T> search(String field, Map<String, Object> map, Map<String, Operation> operationMap) {
		return getSearchExecutor().searchForList(T, field, operationMap, map, null, T);
	}

	@Override
	public List<T> search(String field, Map<String, Object> map, Map<String, Operation> operationMap, boolean isOrder) {
		return getSearchExecutor().searchForList(T, field, operationMap, map, isOrder, T);
	}
}
