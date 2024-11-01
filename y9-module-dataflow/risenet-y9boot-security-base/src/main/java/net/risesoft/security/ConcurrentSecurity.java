package net.risesoft.security;

import java.util.List;

import net.risesoft.security.pojo.DataUser;

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

	public ConcurrentSecurity(DataUser user, List<String> jobTypes, List<String> environments, boolean userManager, boolean systemManager) {
		this.user = user;
		this.systemManager = systemManager;
		this.userManager = userManager;
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
