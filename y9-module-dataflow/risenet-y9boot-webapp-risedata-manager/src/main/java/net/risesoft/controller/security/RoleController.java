package net.risesoft.controller.security;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.aop.CheckResult;
import net.risesoft.api.persistence.model.security.Role;
import net.risesoft.api.persistence.security.RoleService;
import net.risesoft.pojo.Y9Result;

/**
 * @Description : 角色表
 * @ClassName RoleController
 * @Author lb
 * @Date 2022/8/3 10:28
 * @Version 1.0
 */
@RestController()
@RequestMapping("/api/rest/role")
public class RoleController {
	
	@Autowired
	RoleService roleService;

	/**
	 * 获取所有用户
	 *
	 * @param token
	 * @return
	 */
	@GetMapping("search")
	public Y9Result<Object> search(Role role, LPageable page) {
		return Y9Result.success(roleService.searchForPage(role, page));
	}

	/**
	 * 创建角色 不能标识为必须需要用户
	 *
	 * @param user
	 * @param name
	 * @param token
	 * @return
	 */
	@CheckResult
	@PostMapping("saveRole")
	public Y9Result<Object> saveRole(@RequestBody @Valid Role role, BindingResult result) {
		roleService.saveRole(role);
		return Y9Result.success("0");

	}

	/**
	 * 删除角色
	 *
	 * @param userName 账户
	 * @param token    token
	 * @return
	 */
	@PostMapping("deleteRole")
	public Y9Result<Object> deleteUser(String id) {
		roleService.deleteByRoleId(id);
		return Y9Result.success("0");
	}

	/**
	 * 获取角色信息
	 *
	 * @param userName 账户
	 * @param token    token
	 * @return
	 */
	@GetMapping("getRole")
	public Y9Result<Object> getRole(String id) {
		return Y9Result.success(roleService.findById(id));
	}

}
