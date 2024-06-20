package net.risesoft.api.security;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.api.persistence.model.security.DataUser;
import net.risesoft.api.persistence.model.security.Role;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9public.repository.DataBusinessRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 缓存信息
 *
 */
public class ConcurrentSecurity {
	/**
	 * 当前用户对象
	 */
	private DataUser user;

	/**
	 * 拥有的任务类型
	 */
	private List<String> jobTypes;
	/**
	 * 拥有的环境集合,分割
	 */
	private List<String> environments;
	/**
	 * 是否为用户配置管理员
	 */
	private boolean userManager;
	/**
	 * 是否为 系统管理员
	 */
	private boolean systemManager;

	public ConcurrentSecurity(DataUser user, List<Role> roles) {
		try {
			this.user = user;
			List<String> environments = new ArrayList<String>();
			List<String> jobTypes = new ArrayList<String>();
			for (Role role : roles) {
				if (StringUtils.isNotBlank(role.getEnvironments())) {
					environments.addAll(Arrays.asList(role.getEnvironments().split(",")));
				}
				if (StringUtils.isNotBlank(role.getJobTypes())) {
					DataBusinessRepository dataBusinessRepository = Y9Context.getBean("dataBusinessRepository");
					String[] ids = role.getJobTypes().split(",");
					jobTypes.addAll(Arrays.asList(ids));
					for(String id : ids) {
						jobTypes.addAll(dataBusinessRepository.findByParentId(id));
					}
				}
				if (role.getSystemManager() == 1) {
					systemManager = true;
				}
				if (role.getUserManager() == 1) {
					userManager = true;
				}
			}
			this.environments = environments;
			this.jobTypes = jobTypes;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DataUser getUser() {
		return user;
	}

	public List<String> getJobTypes() {
		return jobTypes;
	}

	public List<String> getEnvironments() {
		return environments;
	}

	public boolean isUserManager() {
		return userManager;
	}

	public boolean isSystemManager() {
		return systemManager;
	}

}
