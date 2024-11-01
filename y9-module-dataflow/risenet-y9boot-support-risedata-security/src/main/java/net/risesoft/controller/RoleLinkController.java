package net.risesoft.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import net.risedata.jdbc.search.LPageable;
import net.risesoft.pojo.Y9Result;
import net.risesoft.security.model.RoleUserLink;
import net.risesoft.security.model.DataUser;
import net.risesoft.security.service.RoleLinkService;
import net.risesoft.security.service.UserService;

/**
 * @Description : 角色用户关联
 * @ClassName RoleLinkController
 * @Author lb
 * @Date 2022/8/3 10:28
 * @Version 1.0
 */
@RestController()
@RequestMapping("/api/rest/role/link")
public class RoleLinkController {
	
	@Autowired
	private RoleLinkService roleLinkService;
	
	@Autowired
	private UserService userService;

	@RequestMapping("/searchUsers")
	public Y9Result<Object> searchUsers(String roleId, DataUser betaUser, LPageable page, Boolean isNot) {
		return Y9Result.success(userService.searchForPageRole(betaUser, page, roleId, isNot));
	}

	@PostMapping("/bind")
	public Y9Result<Object> bind(@RequestBody @Valid RoleUserLink roleUserLink) {
		return Y9Result.success(roleLinkService.save(roleUserLink));
	}

	@PostMapping("/unBind")
	public Y9Result<Object> unBind(@RequestBody @Valid RoleUserLink roleUserLink) {
		return Y9Result.success(roleLinkService.delete(roleUserLink));
	}
}
