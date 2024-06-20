package net.risesoft.controller.security;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.aop.CheckResult;
import net.risesoft.api.persistence.model.security.Role;
import net.risesoft.api.persistence.security.RoleService;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataBusinessService;

@RestController()
@RequestMapping("/api/rest/role")
public class RoleController {
	
	@Autowired
	RoleService roleService;
	
	@Autowired
	private DataBusinessService dataBusinessService;

	/**
	 * 获取所有用户
	 *
	 * @param token
	 * @return
	 */
	@GetMapping("search")
	public Y9Result<Object> search(Role role, LPageable page) {
		LPage<Role> rolePage = roleService.searchForPage(role, page);
		for(Role row : rolePage.getContent()) {
			// 获取业务分类信息
			if(StringUtils.isNotBlank(row.getJobTypes())) {
				String[] ids = row.getJobTypes().split(",");
				String names = "";
				for(String id : ids) {
					String name = dataBusinessService.getById(id).getName();
					names += StringUtils.isBlank(names) ? name : "," + name;
				}
				row.setTypeNames(names);
			}else {
				row.setTypeNames("");
			}
		}
		return Y9Result.success(rolePage);
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
	public Y9Result<String> saveRole(@RequestBody @Valid Role role, BindingResult result) {
		try {
			roleService.saveRole(role);
		} catch (Exception e) {
			return Y9Result.failure(e.getMessage());
		}
		return Y9Result.success("保存成功");
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
		Role role = roleService.findById(id);
		// 获取业务分类信息
		if(StringUtils.isNotBlank(role.getJobTypes())) {
			String[] ids = role.getJobTypes().split(",");
			String names = "";
			for(String id2 : ids) {
				String name = dataBusinessService.getById(id2).getName();
				names += StringUtils.isBlank(names) ? name : "," + name;
			}
			role.setTypeNames(names);
		}else {
			role.setTypeNames("");
		}
		return Y9Result.success(role);
	}

}
