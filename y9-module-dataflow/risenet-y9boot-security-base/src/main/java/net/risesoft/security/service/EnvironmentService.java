package net.risesoft.security.service;

import java.util.List;

import net.risesoft.security.model.Environment;

/**
 * @Description : 环境操作
 * @ClassName RoleService
 * @Author lb
 * @Date 2022/8/4 10:08
 * @Version 1.0
 */
public interface EnvironmentService {
	/**
	 * 查询全部
	 * 
	 * @return
	 */
	List<Environment> findAll();

	/**
	 * 添加
	 * 
	 * @param role
	 */
	void insertEnvironment(Environment role);

	/**
	 * 删除
	 * 
	 * @param id
	 */
	void delById(String id);

	List<Environment> findForEnvironment(List<String> environments);

	/**
	 * 根据环境id 获取环境
	 * 
	 * @param environment
	 * @return
	 */
	String getEnvironmentById(String environment);

	/**
	 * 根据环境Name获取环境
	 * 
	 * @param environment
	 * @return
	 */
	String getEnvironmentByName(String environment);

	List<String> findNameByIds(List<String> environments);

	void updateEnvironment( Environment environment);
}
