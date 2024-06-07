package net.risesoft.api.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.PatternMatchUtils;

import net.risesoft.api.persistence.model.security.DataUser;
import net.risesoft.api.persistence.model.security.Environment;
import net.risesoft.api.persistence.model.security.Role;
import net.risesoft.api.persistence.security.EnvironmentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description : 安全
 * @ClassName Security
 * @Author lb
 * @Date 2022/8/5 10:49
 * @Version 1.0
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
		this.user = user;
		List<String> environments = new ArrayList<String>();
		List<String> jobTypes = new ArrayList<String>();
		for (Role role : roles) {
			if (StringUtils.isNoneBlank(role.getEnvironments())) {
				environments.addAll(Arrays.asList(role.getEnvironments().split(",")));
			}
			if (StringUtils.isNoneBlank(role.getJobTypes())) {
				jobTypes.addAll(Arrays.asList(role.getJobTypes().split(",")));
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
