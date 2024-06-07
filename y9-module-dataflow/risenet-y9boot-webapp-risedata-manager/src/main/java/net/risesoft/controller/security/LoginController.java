package net.risesoft.controller.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import cn.hutool.core.codec.Base64;
import net.risesoft.api.persistence.security.TokenService;
import net.risesoft.api.persistence.security.UserService;
import net.risesoft.api.security.SecurityManager;
import net.risesoft.pojo.Y9Result;

/**
 * @Description : 登录
 * @ClassName LoginController
 * @Author lb
 * @Date 2022/8/3 10:28
 * @Version 1.0
 */
@RestController
@RequestMapping("/services/rest/login")
public class LoginController {
	
	@Autowired
	UserService userService;

	@Autowired
	TokenService tokenService;
	
	/**
	 * @param userName 用户名
	 * @param password 密码加密后
	 * @return
	 */
	@PostMapping("getToken")
	public Y9Result<Object> getToken(
			@RequestParam(required = true)  String account,
			@RequestParam(required = true)  String password) {
		return Y9Result.success(tokenService.createToken(userService.checkLogin(account, Base64.decodeStr(password))));
	}

	@Autowired
	SecurityManager securityManager;

	
	@GetMapping("getUser")
	public Y9Result<Object> getUser() {
		return Y9Result.success(securityManager.getConcurrentSecurity().getUser());
	}

	/**
	 * 删除掉token
	 * 
	 * @param token
	 * @return
	 */
	@PostMapping("removeToken")
	public Y9Result<Object> removeToken() {
		return Y9Result.success(securityManager.removeToken(securityManager.getToken()));
	}

}
