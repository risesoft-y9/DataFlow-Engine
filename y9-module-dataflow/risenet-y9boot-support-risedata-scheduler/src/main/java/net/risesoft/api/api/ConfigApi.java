package net.risesoft.api.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.risedata.rpc.provide.annotation.API;
import net.risedata.rpc.provide.annotation.RPCServer;
import net.risesoft.api.persistence.config.ConfigService;
import net.risesoft.api.persistence.model.config.Config;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.repository.DataSingleTaskConfigRepository;
import net.risesoft.y9public.repository.DataTaskRepository;

/**
 * 提供配置文件api的操作
 * 
 * @typeName ConfigApi
 * @date 2024年2月21日
 * @author lb
 */
@Component
@RPCServer(name = "config", enableRequest = true)
public class ConfigApi {
	
	@Autowired
	ConfigService configService;
	
	@Autowired
	private DataTaskRepository dataTaskRepository;
	
	@Autowired
	private DataSingleTaskConfigRepository dataSingleTaskConfigRepository;
	
	/**
	 * 根据jobId 返回配置
	 * 
	 * @param jobId
	 * @return
	 */
	@API
	public Config getConfig(String jobId) {
		return configService.findOneNoSecurity(jobId);
	}
	
	/**
	 * 根据任务id获取任务类型
	 * @param id
	 * @return
	 */
	@API
	public Integer taskType(String id) {
		return dataTaskRepository.findById(id).orElse(null).getTaskType();
	}
	
	/**
	 * 获取单任务的配置
	 * @param id
	 * @return
	 */
	@API
	public Config getSingleConfig(String id) {
		String json = dataSingleTaskConfigRepository.findById(id).orElse(null).getConfigData();
		return Y9JsonUtil.readValue(json, Config.class);
	}

}
