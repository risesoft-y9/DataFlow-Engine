package net.risesoft.api.security.checks;

import org.springframework.stereotype.Component;

import net.risesoft.api.security.ConcurrentSecurity;
import net.risesoft.api.security.SecurityCheck;
import net.risesoft.api.security.SecurityConfig;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description : 是否可以管理用户角色
 * @ClassName HasUserManager
 * @Author lb
 * @Date 2022/8/10 9:14
 * @Version 1.0
 */
@Component
public class HasUserManager implements SecurityCheck {
	@Override
	public boolean check(SecurityConfig config, ConcurrentSecurity security, String url, HttpServletRequest request) {
		return security.isUserManager();
	}
}
