package net.risesoft.api.persistence.security.impl;

import net.risedata.jdbc.factory.ObjectBuilderFactory;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.api.exceptions.ServiceOperationException;
import net.risesoft.api.persistence.dao.RoleDao;
import net.risesoft.api.persistence.model.security.Role;
import net.risesoft.api.persistence.model.security.RoleUserLink;
import net.risesoft.api.persistence.security.RoleLinkService;
import net.risesoft.api.persistence.security.RoleService;
import net.risesoft.api.utils.AutoIdUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description :
 * @ClassName RoleServiceImpl
 * @Author lb
 * @Date 2022/8/4 10:09
 * @Version 1.0
 */
@Service
public class RoleServiceImpl extends AutomaticCrudService<Role, String>
		implements RoleService, ApplicationListener<ApplicationStartedEvent> {

	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ADMIN_ID = "1";

	@Autowired
	RoleDao roleDao;

	@Transactional
	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		if (roleDao.hasAdminRole() == 0) {
			Role role = new Role();
			role.setName(ROLE_ADMIN);
			role.setId(ADMIN_ID);
			role.setSystemManager(1);
			role.setUserManager(1);
			role.setEnvironments("");
			role.setJobTypes("");
			insert(role);
		}
	}

	@Override
	public void saveRole(Role role) {
		if (roleDao.hasName(role.getName(), role.getId() == null ? "NULL" : role.getId()) > 0) {
			throw new ServiceOperationException("角色名:" + role.getName() + "已存在!");
		}

		if (role.getId() == null) {
			role.setId(AutoIdUtil.getRandomId26());
			insert(role);
		} else {
			updateById(role);
		}
	}

	@Autowired
	RoleLinkService roleLinkService;

	@Override
	public void deleteByRoleId(String id) {
		if (id == ADMIN_ID) {
			throw new ServiceOperationException("系统超级管理员不能被删除");
		}
		deleteById(id);
		roleLinkService.delete(ObjectBuilderFactory.builder(RoleUserLink.class).builder("roleId", id));
	}

	@Override
	public List<Role> getRolesByUser(String id) {
		return roleDao.getRolesByUser(id);
	}

	@Override
	public Role findById(String id) {
		return getOne(id);
	}

}
