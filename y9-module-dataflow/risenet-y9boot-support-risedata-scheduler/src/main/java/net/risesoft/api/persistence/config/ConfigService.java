package net.risesoft.api.persistence.config;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.persistence.model.config.Config;

import java.util.List;
import java.util.Map;

/**
 * @Description : 配置文件 服务
 * @ClassName ConfigService
 * @Author lb
 * @Date 2022/8/5 10:10
 * @Version 1.0
 */
public interface ConfigService {
	/**
	 * 修改/保存 配置文件 如果是
	 *
	 * @param config
	 */
	void saveConfig(Config config);

	/**
	 * 查询配置文件
	 * 
	 * @param groups      分组
	 * @param configs     配置文件名
	 * @param environment 环境
	 * @return
	 */
	List<String> findIdAll(String[] groups, String[] configs, String environment);

	/**
	 * 根据环境 查找并分页
	 *
	 * @param config   配置信息
	 * @param pageable 分页信息
	 */
	LPage<Map<String, Object>> search(Config config, LPageable pageable);

	/**
	 * 更具id 删除配置
	 *
	 * @param id
	 */
	void delConfigById(String id);

	/**
	 * 回滚版本 回滚
	 *
	 * @param hisId 历史id
	 * @param id    配置文件id
	 * @return
	 */
	boolean back(String hisId, String id);

	/**
	 * 根据id集合查询
	 *
	 * @param ids
	 * @return
	 */
	List<Config> findByIds(String[] ids);

	/**
	 * 获取map
	 *
	 * @param groups      分组集合
	 * @param configs     配置文件
	 * @param environment 环境
	 * @param ipAddress   ipaddr
	 * @return
	 */
	Map<String, Object> getConfigMap(String[] groups, String[] configs, String environment, String serviceId,
			String ipAddress, boolean append);

	/**
	 * 同步到其他环境
	 * 
	 * @param sourceConfigId
	 * @param toEnvironment
	 * @return
	 */
	boolean syncEnvironment(String sourceConfigId, String toEnvironment);

	/**
	 * 查询配置文件
	 * 
	 * @param id
	 * @return
	 */
	Config findOneNoSecurity(String id);

	/**
	 * 获取一个配置文件绕过权限
	 * 
	 * @param name        配置文件名
	 * @param environment
	 * @return
	 */
	Config findOneNoSecurity(String name, String environment);

	/**
	 * 刷新/保存配置
	 * 
	 * @param jobId
	 */
	void refreshConfig(String jobId,String taskName);
}
