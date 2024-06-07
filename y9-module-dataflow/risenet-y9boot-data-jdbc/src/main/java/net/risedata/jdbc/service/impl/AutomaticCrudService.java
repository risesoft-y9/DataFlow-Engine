package net.risedata.jdbc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import net.risedata.jdbc.executor.delete.DeleteExecutor;
import net.risedata.jdbc.executor.insert.InsertExecutor;
import net.risedata.jdbc.executor.search.SearchExecutor;
import net.risedata.jdbc.executor.update.UpdateExecutor;

/**
 * 自动注入了默认的执行器的service
 * 
 * @author libo 2021年5月27日
 * @param <T>
 * @param <ID>
 */
public class AutomaticCrudService<T, ID> extends CrudServiceImpl<T, ID> {
	@Autowired
	private SearchExecutor searchExecutor;
	@Autowired
	private InsertExecutor insertExecutor;
	@Autowired
	private UpdateExecutor updateExecutor;
	@Autowired
	private DeleteExecutor deleteExecutor;

	@Override
	public SearchExecutor getSearchExecutor() {
		return searchExecutor;
	}

	@Override
	public InsertExecutor getInsertExecutor() {
		return insertExecutor;
	}

	@Override
	public DeleteExecutor getDeleteExecutor() {
		return deleteExecutor;
	}

	@Override
	public UpdateExecutor getUpdateExecutor() {
		return updateExecutor;
	}

	@Override
	public Class<T> getT() {
		return T;
	}

	@Override
	public List<T> searchAll() {
		return getSearchExecutor().searchForList(T, T);
	}

	@Override
	public List<T> findByIds(ID... ids) {
		// TODO Auto-generated method stub
		return getSearchExecutor().findByIds(T, T, ids);
	}

}
