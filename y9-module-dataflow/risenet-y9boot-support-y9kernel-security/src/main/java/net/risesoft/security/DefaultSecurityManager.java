package net.risesoft.security;

import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risesoft.security.RPCRequestFilter;
import net.risesoft.security.SecurityConfig;
import net.risesoft.security.pojo.DataUser;
import net.risesoft.util.IpUtils;
import net.risesoft.util.PattenUtil;
import net.risesoft.util.Y9KernelApiUtil;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DefaultSecurityManager implements SecurityManager, Filter {
	
	/**
	 * 存放着 当前线程上的token 信息
	 */
	private ThreadLocal<Object> threadLocal = new ThreadLocal<>();

	/**
	 * 存放着Token => 安全信息
	 */
	private ConcurrentHashMap<String, ConcurrentSecurity> TOKEN_SECURITY_MAP = new ConcurrentHashMap<>();

	@Override
	public ConcurrentSecurity getConcurrentSecurity() {
		Object token = threadLocal.get();
		if (token == null) {
			return null;
		}
		if (token instanceof String) {
			threadLocal.set(TOKEN_SECURITY_MAP.get(token));
		}
		return ((ConcurrentSecurity) threadLocal.get());
	}

	@Override
	public boolean hasMatch(String source, String compare) {
		return PatternMatchUtils.simpleMatch(source, compare);
	}

	@Override
	public boolean hasMatch(String[] source, String compare) {
		for (String s : source) {
			if (hasMatch(s, compare)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void tokenFailure(List<String> failureTokens, Long time) {
		
	}

	@Override
	public boolean removeToken(String token) {
		return false;
	}

	@Override
	public String getConcurrentIp() {
		RPCRequestContext current = RPCRequestFilter.getCurrent();
		if (current != null) {
			return ((InetSocketAddress) current.getConcurrentConnection().getRemoteAddress()).getHostString();
		} else {
			HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
			return IpUtils.getIPAddress(request);
		}
	}

	public static Y9Result<Object> tokenError = Y9Result.failure(401, "no token or Token expired");
	public static Y9Result<Object> noPermission = Y9Result.failure(403, "no permission");

	/**
	 * 安全配置链条
	 */
	@Autowired(required = false)
	private List<SecurityConfig> securityConfigs;
	
	/**
	 * rpc 和注册相关http 跳过
	 */
	public String[] excludeStartUrls = { "/RPC/", "/register/" };
	/**
	 * getToken 接口放开
	 */
	public String[] excludeEndUrls = { "getToken", "/register/", "getTestData", "saveTestData" };

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletRequest request1 = ((HttpServletRequest) request);
			//检查人员登录信息
			String token = Y9LoginUserHolder.getPersonId();
			String url = request1.getRequestURI();
			if (StringUtils.isEmpty(token)) {
				for (String excludeStartUrl : excludeEndUrls) {
					if (url.endsWith(excludeStartUrl)) {
						chain.doFilter(request, response);
						return;
					}
				}
				for (String excludeStartUrl : excludeStartUrls) {
					if (url.startsWith(excludeStartUrl)) {
						chain.doFilter(request, response);
						return;
					}
				}
				throwError(tokenError, request, response);
				return;
			}
			// 缓存登录人员权限信息
			saveSecurity(token);
			// 检查访问的url用户有没有权限
			for (SecurityConfig securityConfig : securityConfigs) {
				if (PattenUtil.hasMatch(securityConfig.getCheckUrl(), url)) {
					if (PattenUtil.hasMatch(securityConfig.getWhiteList(), url)) {
						continue;
					}
					if (!securityConfig.getSecurityCheck().check(securityConfig, getConcurrentSecurity(), url, request1)) {
						throwError(noPermission, request, response);
						return;
					}
				}
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
			throwError(Y9Result.failure(500, e.getMessage()), request, response);
		} finally {
			threadLocal.remove();
		}
	}

	private void saveSecurity(String token) throws Exception {
		DataUser user = new DataUser();
		user.setId(token);
		user.setUserName(Y9LoginUserHolder.getUserInfo().getName());
		user.setAccount(Y9LoginUserHolder.getUserInfo().getLoginName());
		
		LOGGER.debug("获取用户["+Y9LoginUserHolder.getUserInfo().getName()+"]权限-开始");
		// 获取用户的权限
		boolean userManager = false;
		boolean systemManager = Y9KernelApiUtil.hasRole(Y9LoginUserHolder.getTenantId(), token, "系统管理员");
		// 环境角色
		List<String> envRoles = new ArrayList<String>();
		String[] environments = Y9Context.getProperty("y9.common.environments", "Public,dev").split(",");
		for(String env : environments) {
			boolean hasRole = Y9KernelApiUtil.hasRole2(Y9LoginUserHolder.getTenantId(), token, env);
			if(hasRole) {
				envRoles.add(env);
			}
		}
		// 数据目录
		List<Map<String, Object>> dataCatalogList = Y9KernelApiUtil.getDataCatalogTree(Y9LoginUserHolder.getTenantId(), token, true);
		if(dataCatalogList.size() == 0) {
			throw new Exception("当前用户没有权限，请联系管理员");
		}
		List<String> jobTypes = dataCatalogList.stream().map(map -> (String) map.get("id")).collect(Collectors.toList());
		LOGGER.debug("获取用户["+Y9LoginUserHolder.getUserInfo().getName()+"]权限-结束");
		
		ConcurrentSecurity concurrentSecurity = new ConcurrentSecurity(user, jobTypes, envRoles, userManager, systemManager);
		threadLocal.set(token);
		TOKEN_SECURITY_MAP.put(token, concurrentSecurity);
	}

	private void throwError(Y9Result<?> result, ServletRequest request, ServletResponse response) throws IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		response.getWriter().write(JSON.toJSONString(result));
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public String getToken() {
		return "";
	}

}
