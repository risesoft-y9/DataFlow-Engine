package net.risesoft.security.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 角色信息
 * @author pzx
 *
 */
public class RoleDTO {

	private String id;
	
	@NotBlank(message = "角色名字不能为空")
	private String name;

    @NotBlank(message = "环境操作权限不能为空")
	private String environments;
	
	private String jobTypes;// 业务类型ids
	
    @NotNull(message = "用户权限管理不能为空")
	private Integer userManager;
	
	@NotNull(message = "系统管理权限不能为空")
	private Integer systemManager;
    
	private String typeNames;// 存业务类型名称

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnvironments() {
		return environments;
	}

	public void setEnvironments(String environments) {
		this.environments = environments;
	}

	public String getJobTypes() {
		return jobTypes;
	}

	public void setJobTypes(String jobTypes) {
		this.jobTypes = jobTypes;
	}

	public Integer getUserManager() {
		return userManager;
	}

	public void setUserManager(Integer userManager) {
		this.userManager = userManager;
	}

	public Integer getSystemManager() {
		return systemManager;
	}

	public void setSystemManager(Integer systemManager) {
		this.systemManager = systemManager;
	}

	public String getTypeNames() {
		return typeNames;
	}

	public void setTypeNames(String typeNames) {
		this.typeNames = typeNames;
	}

}
