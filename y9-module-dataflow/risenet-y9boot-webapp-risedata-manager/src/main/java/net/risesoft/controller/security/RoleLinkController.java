package net.risesoft.controller.security;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.aop.CheckResult;
import net.risesoft.api.persistence.model.security.DataUser;
import net.risesoft.api.persistence.model.security.RoleUserLink;
import net.risesoft.api.persistence.security.RoleLinkService;
import net.risesoft.api.persistence.security.UserService;
import net.risesoft.pojo.Y9Result;

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
	RoleLinkService roleLinkService;
	
	@Autowired
	UserService userService;

	@RequestMapping("/searchUsers")
	public Y9Result<Object> searchUsers(String roleId, DataUser betaUser, LPageable page, Boolean isNot) {
		return Y9Result.success(userService.searchForPageRole(betaUser, page, roleId, isNot));
	}

	@CheckResult
	@PostMapping("/bind")
	public Y9Result<Object> bind(@RequestBody @Valid RoleUserLink roleUserLink, BindingResult result) {
		return Y9Result.success(roleLinkService.save(roleUserLink));
	}

	@CheckResult
	@PostMapping("/unBind")
	public Y9Result<Object> unBind(@RequestBody @Valid RoleUserLink roleUserLink, BindingResult result) {
		return Y9Result.success(roleLinkService.delete(roleUserLink));
	}
}
