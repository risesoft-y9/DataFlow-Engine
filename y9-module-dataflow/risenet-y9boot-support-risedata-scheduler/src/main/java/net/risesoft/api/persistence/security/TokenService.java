package net.risesoft.api.persistence.security;

import java.util.List;

import net.risesoft.api.persistence.model.security.DataUser;

/**
 * @Description : 令牌服务
 * @ClassName TokenService
 * @Author lb
 * @Date 2022/8/3 15:27
 * @Version 1.0
 */
public interface TokenService {
	/**
	 * 根据 user 创建token
	 *
	 * @param userModel
	 * @return
	 */
	String createToken(DataUser userModel);

	/**
	 * 根据token 获取用户
	 *
	 * @param token tokenid
	 * @return
	 */
	DataUser getUserByToken(String token);

	/**
	 * 续订
	 *
	 * @param token token
	 * @param time  最新时间
	 * @return
	 */
	boolean renew(String token, long time);

	/**
	 * @param token token
	 * @param time  过期时间
	 * @return
	 */
	boolean deleteToken(String token, long time);

	/**
	 * 拿到最新的过期时间
	 * 
	 * @return
	 */
	long getFailureTime();

	/**
	 * 删除一个token
	 * 
	 * @param token
	 * @return
	 */
	boolean removeToken(String token);

	/**
	 * 获取失效的token
	 * 
	 * @return
	 */
	List<String> getFailureToken();
}
