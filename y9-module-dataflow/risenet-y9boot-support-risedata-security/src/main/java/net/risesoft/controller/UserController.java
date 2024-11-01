package net.risesoft.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.hutool.core.codec.Base64;
import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.security.SecurityManager;
import net.risesoft.security.model.DataUser;
import net.risesoft.security.service.UserService;
import net.risesoft.pojo.Y9Result;

/**
 * @Description : 用户
 * @ClassName UserController
 * @Author lb
 * @Date 2022/8/3 10:28
 * @Version 1.0
 */
@RestController()
@RequestMapping("/api/rest/user")
public class UserController {
	
	@Autowired
	private UserService userService;

	@RequestMapping("searchForPage")
	public Y9Result<LPage<DataUser>> searchForPage(DataUser betaUser, LPageable lPageable) {
		return Y9Result.success(userService.searchForPage(betaUser, lPageable));
	}

	/**
	 * 创建新用户
	 *
	 * @param userName 用户名
	 * @param password 密码 base64加密过来
	 * @param account  账号
	 * @return
	 */
	@PostMapping("createUser")
	public Y9Result<Object> createUser(@Valid @RequestBody DataUser dataUser) {
		dataUser.setPassword(Base64.decodeStr(dataUser.getPassword()));
		return Y9Result.success(userService.createUser(dataUser));
	}

	/**
	 * 修改用户信息
	 *
	 * @param betaUser 用户
	 * @return
	 */
	@PostMapping("updateUserInfo")
	public Y9Result<Object> updateUserInfo(@Valid DataUser betaUser) {
		return Y9Result.success(userService.updateInfoById(betaUser));
	}

	/**
	 * 修改密码
	 *
	 * @param userName 账户
	 * @param password 密码BASE64 无需MD5
	 * @param token    token
	 * @return
	 */
	@PostMapping("updatePassword")
	public Y9Result<Object> updatePassword(@RequestParam(required = true) String id,
			@RequestParam(required = true) String password) {
		userService.updatePassword(id, Base64.decodeStr(password));
		return Y9Result.success(1);

	}

	@Autowired
	SecurityManager securityManager;

	/**
	 * 删除用户
	 *
	 * @param userName 账户
	 * @param token    token
	 * @return
	 */
	@PostMapping("deleteUser")
	public Y9Result<Object> deleteUser(@RequestParam(required = true) String id) {
		return Y9Result.success(userService.deleteUser(id));
	}

	/**
	 * 获取用户信息
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("getUser")
	public Y9Result<Object> getUser(@RequestParam(required = true) String id) {
		DataUser dataUser = userService.findOne(id);
		dataUser.setPassword("");
		return Y9Result.success(dataUser);
	}

}
