package net.risesoft.security.checks;

import org.springframework.stereotype.Component;

import net.risesoft.security.ConcurrentSecurity;
import net.risesoft.security.SecurityCheck;
import net.risesoft.security.SecurityConfig;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description : 检查是否有安全操作权限 安全管理包含,白名单,服务器,环境
 * @ClassName HasSecurityManager
 * @Author lb
 * @Date 2022/8/9 15:17
 * @Version 1.0
 */
@Component
public class HasSecurityManager implements SecurityCheck {

    @Override
    public boolean check(SecurityConfig config, ConcurrentSecurity security, String url, HttpServletRequest request) {
        return security.isSystemManager();
    }
}
