package net.risesoft.api.security;

import java.util.List;

/**
 * @Description : 安全配置
 * @ClassName SecurityConfig
 * @Author lb
 * @Date 2022/8/9 14:34
 * @Version 1.0
 */
public class SecurityConfig {
    /**
     * 跳过检查
     */
    private List<String> whiteList;
    /**
     * 检查的url
     */
   private List<String> checkUrl;
    /**
     * 检查器
     */
   private SecurityCheck securityCheck;

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public List<String> getCheckUrl() {
        return checkUrl;
    }

    public void setCheckUrl(List<String> checkUrl) {
        this.checkUrl = checkUrl;
    }

    public SecurityCheck getSecurityCheck() {
        return securityCheck;
    }

    public void setSecurityCheck(SecurityCheck securityCheck) {
        this.securityCheck = securityCheck;
    }

    @Override
    public String toString() {
        return "SecurityConfig{" +
                "whiteList=" + whiteList +
                ", checkUrl=" + checkUrl +
                ", securityCheck=" + securityCheck +
                '}';
    }
}

