package net.risesoft.security;

import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risesoft.exceptions.TokenException;
import net.risesoft.security.RPCRequestFilter;
import net.risesoft.security.SecurityConfig;
import net.risesoft.util.IpUtils;
import net.risesoft.util.PattenUtil;
import net.risesoft.pojo.Y9Result;
import net.risesoft.security.model.Role;
import net.risesoft.security.pojo.DataUser;
import net.risesoft.security.service.RoleService;
import net.risesoft.security.service.TokenService;
import net.risesoft.y9public.repository.DataBusinessRepository;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

/**
 * @Description : 安全管理器
 * @ClassName DefaultSecurityManager
 * @Author lb
 * @Date 2022/8/5 10:47
 * @Version 1.0
 */
@Service
public class DefaultSecurityManager implements SecurityManager, Filter {
	
	/**
	 * 存放着 当前线程上的token 信息
	 */
	private ThreadLocal<Object> threadLocal = new ThreadLocal<>();

	/**
	 * 存放着Token => 安全信息
	 */
	private ConcurrentHashMap<String, ConcurrentSecurity> TOKEN_SECURITY_MAP = new ConcurrentHashMap<>();

	/**
	 * 存放着 token => 最后一次访问时间
	 */
	private Map<String, Long> TOKEN_TIME_MAP = new HashMap<>();

	// 使用定时任务拿到失效的token 如果在时间中查询该token 没有失效 则重新添加到数据库 或 修改token 时间
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

	private List<String> removedToken;

	@Override
	public void tokenFailure(List<String> failureTokens, Long time) {
		Long newTime = 0L;
		boolean isNull = removedToken == null;
		if (isNull) {
			removedToken = new ArrayList<>();
		}
		for (String failureToken : failureTokens) {
			newTime = TOKEN_TIME_MAP.get(failureToken);
			if (newTime == null) {
				if (removedToken.remove(failureToken)) {
					tokenService.deleteToken(failureToken, time);
				} else {
					removedToken.add(failureToken);
				}
			} else {
				if (time < newTime) {
					if (tokenService.renew(failureToken, newTime)) {
					} else {
						System.out.println("续订失败需要新增");
					}
				} else {
					// 删除
					if (tokenService.deleteToken(failureToken, time)) {
					}
					TOKEN_TIME_MAP.remove(failureToken);
					TOKEN_SECURITY_MAP.remove(failureToken);
				}
			}
		}
		if (!isNull) {
			removedToken = null;
		}
	}

	@Override
	public boolean removeToken(String token) {
		boolean b = tokenService.removeToken(token);
		if (b) {
			TOKEN_TIME_MAP.remove(token);
			TOKEN_SECURITY_MAP.remove(token);
		}
		return b;
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

	@Autowired
	private TokenService tokenService;

	@Autowired
	private RoleService roleService;

	public static Y9Result<Object> noToken = Y9Result.failure(401, "no token or Token expired");
	public static Y9Result<Object> tokenError = Y9Result.failure(401, "no token or Token expired");
	public static Y9Result<Object> noPermission = Y9Result.failure(403, "no permission");

	/**
	 * 每1小时判断一次 检查token 状态
	 */
	@Scheduled(cron = "0 0 0/1 * * ? ")
	public void checkFailureToken() {
		Set<String> tokenS = TOKEN_TIME_MAP.keySet();
		Long newTime = null;
		Long failureTime = tokenService.getFailureTime();
		for (Object key : tokenS.toArray()) {
			newTime = TOKEN_TIME_MAP.get(key);
			if (newTime != null && failureTime > newTime) {
				TOKEN_TIME_MAP.remove(key);
				TOKEN_SECURITY_MAP.remove(key);
			}
		}
		List<String> failureTokens = tokenService.getFailureToken();
		tokenFailure(failureTokens, failureTime);
	}

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
			//检查token
			String token = request1.getHeader(TOKEN_KEY);
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
				throwError(noToken, request, response);
				return;
			}
			// 保存登录信息
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
		} catch (TokenException e) {
			throwError(tokenError, request, response);
		} catch (Exception e) {
			throwError(Y9Result.failure(500, e.getMessage()), request, response);
		} finally {
			threadLocal.remove();
		}
	}
	
	@Autowired
	private DataBusinessRepository dataBusinessRepository;

	private void saveSecurity(String token) throws Exception {
		// 获取用户信息
		net.risesoft.security.model.DataUser userToken = tokenService.getUserByToken(token);
		// 获取用户的权限
		List<Role> roles = roleService.getRolesByUser(userToken.getId());
		if(roles.size() == 0) {
			throw new Exception("当前用户没有权限，请联系管理员");
		}
		DataUser user = new DataUser();
		user.setId(userToken.getId());
		user.setAccount(userToken.getAccount());
		user.setUserName(userToken.getUserName());
		
		List<String> environments = new ArrayList<String>();
		List<String> jobTypes = new ArrayList<String>();
		boolean systemManager = false;
		boolean userManager = false;
		for (Role role : roles) {
			if (StringUtils.isNotBlank(role.getEnvironments())) {
				environments.addAll(Arrays.asList(role.getEnvironments().split(",")));
			}
			if (StringUtils.isNotBlank(role.getJobTypes())) {
				String[] ids = role.getJobTypes().split(",");
				jobTypes.addAll(Arrays.asList(ids));
				for(String id : ids) {
					jobTypes.addAll(dataBusinessRepository.findByParentId(id));
				}
			}
			if (role.getSystemManager() == 1) {
				systemManager = true;
			}
			if (role.getUserManager() == 1) {
				userManager = true;
			}
		}
		
		ConcurrentSecurity concurrentSecurity = new ConcurrentSecurity(user, jobTypes, environments, userManager, systemManager);
		threadLocal.set(token);
		TOKEN_SECURITY_MAP.put(token, concurrentSecurity);
		TOKEN_TIME_MAP.put(token, System.currentTimeMillis());
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
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		return request.getHeader(TOKEN_KEY);
	}

}
