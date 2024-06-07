package net.risesoft.api.persistence.config;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.persistence.model.config.Config;
import net.risesoft.api.persistence.model.config.ConfigHis;
import net.risesoft.api.security.ConcurrentSecurity;

import java.util.List;
import java.util.Map;

/**
 * @Description : 历史 配置文件 服务
 * @ClassName ConfigService
 * @Author lb
 * @Date 2022/8/5 10:10
 * @Version 1.0
 */
public interface ConfigHisService {
	/**
	 * 保存 历史配置文件记录
	 *
	 * @param config
	 */
	void saveConfig(Config config, String operation);

	/**
	 * 根据环境 查找并分页
	 *
	 * @param config   配置信息
	 * @param pageable 分页信息
	 */
	LPage<Map<String, Object>> search(ConfigHis config, LPageable pageable);

	/**
	 * 更具id 删除配置
	 *
	 * @param id
	 */
	void delConfigById(String id);

	/**
	 * 根据id 获取配置
	 *
	 * @param id
	 * @return
	 */
	ConfigHis findOne(String id);

	/**
	 * 清理配置文件 (30天之前)
	 */
	void clearHis();

}
