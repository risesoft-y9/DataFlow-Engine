package net.risesoft.security.dao;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;
import net.risesoft.security.model.Environment;

public interface EnvironmentDao extends Repository<Environment> {

	@Search("select count(*) from Y9_DATASERVICE_ENVIRONMENT where name='Public'")
	Integer hasPublic();

	@Search("select count(*) from Y9_DATASERVICE_ENVIRONMENT where  name = ? and id != ?")
	Integer hasByName(String name, String id);

	@Search("select name from Y9_DATASERVICE_ENVIRONMENT where name = ?")
	String findById(String environment);

	@Search("select id from Y9_DATASERVICE_ENVIRONMENT where name = ?")
	String findByName(String name);

	@Modify("update Y9_DATASERVICE_ENVIRONMENT set name=?1,description=?2 where id = ?3")
	Integer updateEnvironment(String name, String description, String id);
	
	@Modify("insert into Y9_DATASERVICE_ENVIRONMENT (ID,NAME,DESCRIPTION) values(?,?,?)")
    Integer create(String id, String name, String description);
}
