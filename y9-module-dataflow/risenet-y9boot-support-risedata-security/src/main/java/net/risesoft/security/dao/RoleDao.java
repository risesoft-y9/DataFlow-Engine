package net.risesoft.security.dao;

import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;
import net.risesoft.security.model.Role;
import net.risesoft.security.service.impl.RoleServiceImpl;

import java.util.List;

/**
 * @Description :
 * @ClassName UserDao
 * @Author lb
 * @Date 2022/8/3 16:04
 * @Version 1.0
 */
public interface RoleDao extends Repository<Role> {

	@Search("select count(*) from Y9_DATASERVICE_ROLE where NAME='" + RoleServiceImpl.ROLE_ADMIN + "'")
	Integer hasAdminRole();

	@Search("select count(*) from Y9_DATASERVICE_ROLE where NAME=? and id!=?")
	Integer hasName(String name, String id);

	@Search("select * from Y9_DATASERVICE_ROLE where id  in  (select ROLE_ID FROM Y9_DATASERVICE_ROLE_USER_LINK where USER_ID=?)")
	List<Role> getRolesByUser(String id);

}
