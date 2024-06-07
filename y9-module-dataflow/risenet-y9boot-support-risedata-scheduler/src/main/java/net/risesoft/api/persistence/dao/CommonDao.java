package net.risesoft.api.persistence.dao;

import java.util.List;

import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;

/**
 * 公共的DAO
 * 
 * @typeName CommonDao
 * @date 2024年1月15日
 * @author lb
 */
public interface CommonDao extends Repository {
	
	@Search("select name from #{?1} where id in (?2)")
	List<String> findNameByIds(Class<?> entiryClass,List<String> ids);

}
