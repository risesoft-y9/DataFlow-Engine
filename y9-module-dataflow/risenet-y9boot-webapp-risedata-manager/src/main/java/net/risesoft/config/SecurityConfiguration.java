package net.risesoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.security.SecurityConfig;
import net.risesoft.security.checks.HasSecurityManager;
import net.risesoft.security.checks.HasUserManager;

import java.util.Arrays;

/**
 * @Description : 安全配置
 * @ClassName SecurityConfiguration
 * @Author lb
 * @Date 2022/8/9 14:40
 * @Version 1.0
 */
@Configuration
public class SecurityConfiguration {

	/**
	 * 系统管理安全类
	 * 
	 * @param hasSecurityManager
	 * @return
	 */
	@Bean
	public SecurityConfig hasSecurityManagerConfig() {
		SecurityConfig securityConfig = new SecurityConfig();
		securityConfig.setSecurityCheck(new HasSecurityManager());
		securityConfig.setCheckUrl(Arrays.asList("**/api/rest/system/**"));// 白名单、环境
		securityConfig.setWhiteList(Arrays.asList("**/api/rest/system/Environment/getEnvironment", "**/api/rest/system/Environment/getAll",
				"**/api/rest/system/networkWhiteList/searchForPage", "**/api/rest/system/service/getServicesAll", 
				"**/api/rest/system/service/getServiceByName"));
		return securityConfig;
	}

	/***
	 * 用户角色管理安全类
	 * 
	 * @param hasUserManager
	 * @return
	 */
	@Bean
	public SecurityConfig hasUserManager() {
		SecurityConfig securityConfig = new SecurityConfig();
		securityConfig.setSecurityCheck(new HasUserManager());
		securityConfig.setCheckUrl(Arrays.asList("**/api/rest/role/**", "**/api/rest/user/**"));// 用户、角色
		securityConfig.setWhiteList(Arrays.asList("**/api/rest/role/search", "**/api/rest/role/link/searchUsers",
				"**/api/rest/user/searchForPage"));
		return securityConfig;
	}

}
