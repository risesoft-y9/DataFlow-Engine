package net.risesoft.api.persistence.model.security;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 角色与用户连接表
 * 
 * @typeName RoleUserLink
 * @date 2024年1月15日
 * @author lb
 */
@Entity
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@Table(name = "Y9_DATASERVICE_ROLE_USER_LINK")
@IdClass(RoleUserLink.class)
public class RoleUserLink implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 角色id
	 */
    @Comment(value = "角色id")
	@Id
	@NotBlank(message = "角色id不能为空")
	@Column(name = "ROLE_ID", length = 26)
	private String roleId;
	/**
	 * 用户id
	 */
	@Id
    @Comment(value = "用户id")
	@NotBlank(message = "用户id不能为空")
	@Column(name = "USER_ID", length = 26)
	private String userId;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
