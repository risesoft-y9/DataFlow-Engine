package net.risesoft.api.persistence.dao;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;
import net.risesoft.api.persistence.model.security.Environment;

import java.util.List;

/**
 * @Description :
 * @ClassName UserDao
 * @Author lb
 * @Date 2022/8/3 16:04
 * @Version 1.0
 */
public interface EnvironmentDao extends Repository {

	@Search("select count(*) from Y9_DATASERVICE_ENVIRONMENT where name='Public'")
	Integer hasPulibc();

	@Search("select count(*) from Y9_DATASERVICE_ENVIRONMENT where  name = ? and id != ?")
	Integer hasByName(String name, String id);

	@Search("select name from Y9_DATASERVICE_ENVIRONMENT where name = ?")
	String findByID(String environment);

	@Search("select id from Y9_DATASERVICE_ENVIRONMENT where name = ?")
	String findByNAME(String name);

	@Modify("update Y9_DATASERVICE_ENVIRONMENT set name=?1,description=?2 where id = ?3")
	Integer updateEnvironment(String name, String description, String id);
}
