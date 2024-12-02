package net.risesoft.api;

import net.risedata.register.rpc.RegisterAPI;
import net.risedata.rpc.consumer.annotation.API;
import net.risedata.rpc.consumer.annotation.RPCClient;

import net.risesoft.model.Config;

/**
 * 提供配置文件api的操作
 * 
 * @typeName ConfigApi
 * @date 2024年2月21日
 * @author lb
 */
@RPCClient(name = "config", managerName = RegisterAPI.MANAGER_NAME)
public interface ConfigApi {
	
	/**
	 * 根据jobId 返回同步任务配置
	 * @param jobId
	 * @return
	 */
	@API
	public Config getConfig(String jobId);
	
	/**
	 * 根据任务id获取任务类型
	 * @param id
	 * @return
	 */
	@API
	public Integer taskType(String id);
	
	/**
	 * 获取单任务的配置
	 * @param id
	 * @return
	 */
	@API
	public Config getSingleConfig(String id);

}
