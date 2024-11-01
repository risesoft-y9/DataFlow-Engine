package net.risesoft.security.service.impl;

import net.risedata.jdbc.factory.ObjectBuilderFactory;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.exceptions.ServiceOperationException;
import net.risesoft.security.dao.RoleDao;
import net.risesoft.security.model.Role;
import net.risesoft.security.model.RoleUserLink;
import net.risesoft.security.service.RoleLinkService;
import net.risesoft.security.service.RoleService;
import net.risesoft.util.AutoIdUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description :
 * @ClassName RoleServiceImpl
 * @Author lb
 * @Date 2022/8/4 10:09
 * @Version 1.0
 */
@Service
public class RoleServiceImpl extends AutomaticCrudService<Role, String> implements RoleService {

	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ADMIN_ID = "1";

	@Autowired
	RoleDao roleDao;

	@Override
	public void saveRole(Role role) {
		if (roleDao.hasName(role.getName(), role.getId() == null ? "NULL" : role.getId()) > 0) {
			throw new ServiceOperationException("角色名:" + role.getName() + "已存在!");
		}

		if (role.getId() == null) {
			role.setId(AutoIdUtil.getRandomId26());
			insert(role);
		} else {
			if(findById(role.getId()) == null) {
				insert(role);
			}else {
				updateById(role);
			}
		}
	}

	@Autowired
	RoleLinkService roleLinkService;

	@Override
	public void deleteByRoleId(String id) {
		if (ADMIN_ID.equals(id)) {
			throw new ServiceOperationException("管理员角色不能删除");
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

	@Override
	public Integer hasAdminRole() {
		return roleDao.hasAdminRole();
	}

}
