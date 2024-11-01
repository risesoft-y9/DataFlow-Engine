package net.risesoft.security.service.impl;

import net.risedata.jdbc.factory.ObjectBuilderFactory;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.security.model.RoleUserLink;
import net.risesoft.security.service.RoleLinkService;

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
public class RoleLinkServiceImpl extends AutomaticCrudService<RoleUserLink, String> implements RoleLinkService {

	@Override
	public List<String> getRole(String userId) {
		return getSearchExecutor().searchForList(ObjectBuilderFactory.builder(RoleUserLink.class, "userId", userId),
				"role_id", null, null, null, String.class);
	}

	@Override
	public List<String> getUsers(String roleId) {
		return getSearchExecutor().searchForList(ObjectBuilderFactory.builder(RoleUserLink.class, "roleId", roleId),
				"User_id", null, null, null, String.class);
	}

}
