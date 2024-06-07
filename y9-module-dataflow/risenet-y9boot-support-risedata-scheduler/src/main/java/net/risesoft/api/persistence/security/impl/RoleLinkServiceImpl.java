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
