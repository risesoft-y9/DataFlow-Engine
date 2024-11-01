package net.risesoft.security.service;

import java.util.List;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.security.model.Role;

/**
 * @Description : 角色表
 * @ClassName RoleService
 * @Author lb
 * @Date 2022/8/4 10:08
 * @Version 1.0
 */
public interface RoleService {
	/**
	 * 查询
	 * 
	 * @return
	 */
	LPage<Role> searchForPage(Role role, LPageable pageable);

	/**
	 * 保存
	 * 
	 * @param role
	 */
	void saveRole(Role role);

	/**
	 * 删除
	 * 
	 * @param role
	 */
	void deleteByRoleId(String id);

	/**
	 * 拿到用户的所有权限
	 * 
	 * @param id
	 * @return
	 */
	List<Role> getRolesByUser(String id);

	/**
	 * 根据id获取角色
	 * 
	 * @param id
	 * @return
	 */
	Role findById(String id);
	
	/**
	 * 判断管理员角色是否存在
	 * @return
	 */
	Integer hasAdminRole();

}
