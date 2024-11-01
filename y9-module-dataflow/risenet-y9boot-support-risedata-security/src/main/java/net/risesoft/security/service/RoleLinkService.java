package net.risesoft.security.service;

import java.util.List;

import net.risesoft.security.model.RoleUserLink;

/**
 * 用户角色权限连接
 * 
 * @typeName RoleLinkService
 * @date 2024年1月15日
 * @author lb
 */
public interface RoleLinkService {

	/**
	 * 获取一个用户的角色
	 * 
	 * @param userId
	 * @return 角色id集合
	 */
	List<String> getRole(String userId);

	/**
	 * 获取所有的用户根据角色id查询
	 * 
	 * @param roleId
	 * @return
	 */
	List<String> getUsers(String roleId);

	/**
	 * 保存一条记录
	 * 
	 * @param roleUserLink
	 * @return
	 */
	int save(RoleUserLink roleUserLink);

	/**
	 * 删除一条
	 * 
	 * @param roleUserLink
	 * @return
	 */
	int delete(RoleUserLink roleUserLink);

}
