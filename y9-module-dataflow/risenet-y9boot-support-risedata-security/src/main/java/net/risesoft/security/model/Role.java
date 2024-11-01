package net.risesoft.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.risedata.jdbc.annotations.operation.Operate;
import net.risedata.jdbc.operation.Operates;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * @Description : 角色表
 * @ClassName Role
 * @Author lb
 * @Date 2022/8/3 15:30
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@Table(name = "Y9_DATASERVICE_ROLE")
@IdClass(Role.class)
public class Role implements Serializable {

	private static final long serialVersionUID = -2951916202754964921L;
	
	@Id
    @Comment(value = "id")
	@Column(name = "ID", length = 100)
	private String id;
	/**
	 * 角色名字 唯一
	 */
    @Comment(value = "角色名字")
	@NotBlank(message = "角色名字不能为空")
	@Operate(value = Operates.EQ)
	@Column(name = "NAME", length = 100)
	private String name;

	/**
	 * 有哪些环境的权限 为 权限id 集合以 ,分割
	 */
    @Comment(value = "环境操作")
    @NotBlank(message = "环境操作权限不能为空")
	@Column(name = "ENVIRONMENTS", length = 200)
	private String environments;
	/**
	 * 有哪些任务类型的权限 用ID 作为集合 以,分割 为空则是全部
	 */
    @Comment(value = "任务类型")
	@Column(name = "JOB_TYPES", length = 200)
	private String jobTypes;
	/**
	 * 是否有安全管理权限也就是用户管理 0代表无1代表有
	 */
    @Comment(value = "用户权限")
    @NotNull(message = "用户权限管理不能为空")
	@Column(name = "USER_MANAGER", length = 1)
	private Integer userManager;
	/**
	 * 是否为系统管理员代表有服务权限和白名单权限以及 环境管理权限
	 */
    @Comment(value = "系统管理员权限")
	@NotNull(message = "系统管理权限不能为空")
	@Column(name = "SYSTEM_MANAGER", length = 1)
	private Integer systemManager;
    
    @Transient
	private String typeNames;// 存任务类型名称

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
