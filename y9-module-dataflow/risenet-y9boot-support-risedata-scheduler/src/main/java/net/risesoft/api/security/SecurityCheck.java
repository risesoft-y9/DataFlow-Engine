package net.risesoft.api.security;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description : 安全检查
 * @ClassName SecurityCheck
 * @Author lb
 * @Date 2022/8/9 14:35
 * @Version 1.0
 */
public interface SecurityCheck {
    /**
     *  检查
     * @param config 配置
     * @param security 当前安全用户
     * @param url url
     * @param request request
     * @return 返回是否通过
     */
    boolean check(SecurityConfig config, ConcurrentSecurity security,String url, HttpServletRequest request);
}
