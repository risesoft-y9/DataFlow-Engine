package net.risesoft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.risedata.jdbc.factory.ObjectBuilderFactory;
import net.risesoft.api.persistence.dao.EnvironmentDao;
import net.risesoft.api.persistence.model.security.DataUser;
import net.risesoft.api.persistence.model.security.Role;
import net.risesoft.api.persistence.model.security.RoleUserLink;
import net.risesoft.api.persistence.security.RoleLinkService;
import net.risesoft.api.persistence.security.RoleService;
import net.risesoft.api.persistence.security.UserService;
import net.risesoft.api.persistence.security.impl.RoleServiceImpl;

@Component
public class OnApplicationReady implements ApplicationListener<ApplicationReadyEvent> {
	
	private static final Logger log = LoggerFactory.getLogger(OnApplicationReady.class);
	
	private final String DEFAULT_USER = "admin";
	private final String DEFAULT_PWD = "admin";
	
	@Autowired
	private EnvironmentDao environmentDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private RoleLinkService roleLinkService;
  
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (environmentDao.hasPublic() == 0) {
			environmentDao.create("Public", "Public", "默认环境");
			environmentDao.create("dev", "dev", "测试环境");
		}
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
		log.info("init data finish");
	}  

}
