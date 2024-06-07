package net.risesoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risesoft.api.security.SecurityConfig;
import net.risesoft.api.security.checks.HasSecurityManager;
import net.risesoft.api.security.checks.HasUserManager;

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
		securityConfig.setCheckUrl(Arrays.asList("/api/rest/system/**"));
		securityConfig.setWhiteList(Arrays.asList("/api/rest/system/Environment/getEnvironment"));
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
		securityConfig.setCheckUrl(Arrays.asList("/api/rest/role/**", "/api/rest/user/**"));
		return securityConfig;
	}

}
