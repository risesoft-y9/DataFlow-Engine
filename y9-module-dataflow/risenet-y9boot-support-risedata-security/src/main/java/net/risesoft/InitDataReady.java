package net.risesoft;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.risedata.jdbc.factory.ObjectBuilderFactory;
import net.risesoft.security.model.Role;
import net.risesoft.security.model.RoleUserLink;
import net.risesoft.security.model.DataUser;
import net.risesoft.security.service.RoleLinkService;
import net.risesoft.security.service.RoleService;
import net.risesoft.security.service.UserService;
import net.risesoft.security.service.impl.RoleServiceImpl;

@Component
@RequiredArgsConstructor
public class InitDataReady implements ApplicationListener<ApplicationReadyEvent> {
	
	private final String DEFAULT_USER = "admin";
	private final String DEFAULT_PWD = "admin";
	
	private final UserService userService;
	
	private final RoleService roleService;
	
	private final RoleLinkService roleLinkService;
  
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (userService.hasName("admin") == 0) {
			DataUser user = new DataUser();
			user.setAccount(DEFAULT_USER);
			user.setUserName(DEFAULT_USER);
			user.setPassword(DEFAULT_PWD);
			roleLinkService.save(ObjectBuilderFactory.builder(RoleUserLink.class, "roleId", RoleServiceImpl.ADMIN_ID)
					.builder("userId", userService.createUser(user)));
		}
		if (roleService.hasAdminRole() == 0) {
			Role role = new Role();
			role.setName(RoleServiceImpl.ROLE_ADMIN);
			role.setId(RoleServiceImpl.ADMIN_ID);
			role.setSystemManager(1);
			role.setUserManager(1);
			role.setEnvironments("");
			role.setJobTypes("");
			roleService.saveRole(role);
		}
	}

}
