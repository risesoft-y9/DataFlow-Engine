package net.risesoft.api.persistence.security;

import java.util.List;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.persistence.model.security.DataUser;

/**
 * @Description : 用户服务
 * @ClassName TokenService
 * @Author lb
 * @Date 2022/8/3 15:27
 * @Version 1.0
 */
public interface UserService {
	/**
	 * 登录状态
	 * 
	 * @param userName 用户名
	 * @param password 密码
	 * @return 返回用户
	 */
	DataUser checkLogin(String account, String password);


	/**
	 * 修改密码 用户id 用户密码
	 * 
	 * @param userId
	 * @param password
	 */
	void updatePassword(String userId, String password);

	/**
	 * 删除用户
	 * 
	 * @param userId 用户id
	 */
	Integer deleteUser(String userId);

	/**
	 * 拿到所有用户
	 * 
	 * @return
	 */
	List<String> findAll();

	/**
	 * 根据id 获取用户
	 * 
	 * @param id
	 * @return
	 */
	DataUser findOne(String id);

	/**
	 * 修改用户信息
	 * 
	 * @param user
	 * @return
	 */
	Integer updateInfoById(DataUser user);

	LPage<DataUser> searchForPage(DataUser betaUser, LPageable lPageable);

	/**
	 * 查询用户是否在某个角色里面/或者排除
	 * 
	 * @param betaUser
	 * @param page
	 * @param roleId
	 * @param isNot
	 * @return
	 */
	LPage<DataUser> searchForPageRole(DataUser betaUser, LPageable page, String roleId, boolean isNot);

	String createUser( DataUser dataUser);
	
	/**
	 * 判断人员是否存在
	 * @param name
	 * @return
	 */
	Integer hasName(String name);

}
