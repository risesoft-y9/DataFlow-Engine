package net.risesoft.security;

import java.util.List;

/**
 * @Description : 安全管理
 * @ClassName SecurityManager
 * @Author lb
 * @Date 2022/8/5 10:45
 * @Version 1.0
 */
public interface SecurityManager {
	
	String TOKEN_KEY = "x-token";

	/**
	 * 获取 当前登录用户以及安全权限
	 * 
	 * @return
	 */
	ConcurrentSecurity getConcurrentSecurity();

	/**
	 * 是否匹配
	 * 
	 * @param source  源
	 * @param compare 匹配对象
	 * @return
	 */
	boolean hasMatch(String source, String compare);

	/**
	 * 是否匹配
	 * 
	 * @param source  匹配源
	 * @param compare 匹配对象
	 * @return
	 */
	boolean hasMatch(String[] source, String compare);

	/**
	 * 过期的token
	 * 
	 * @param failureTokens 过期的token
	 * @param time          过期 的时间 如果在这个时间内有访问则代表未过去需要更新token的时间如果没有访问则删除token
	 *                      同时删除缓存中的token 信息
	 */
	void tokenFailure(List<String> failureTokens, Long time);

	/**
	 * 删除掉一个token
	 * 
	 * @param token
	 * @return
	 */
	boolean removeToken(String token);

	/**
	 * 获取当前ip
	 * 
	 * @return
	 */
	String getConcurrentIp();

	String getToken();
}
