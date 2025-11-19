package net.risesoft.security.dto;

import javax.validation.constraints.NotBlank;

public class RoleUserLinkDTO {
	
	@NotBlank(message = "角色id不能为空")
	private String roleId;
	
	@NotBlank(message = "用户id不能为空")
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
