package net.risesoft.api;

import net.risedata.register.rpc.RegisterAPI;
import net.risedata.rpc.consumer.annotation.API;
import net.risedata.rpc.consumer.annotation.RPCClinet;

import net.risesoft.model.Config;

/**
 * 提供配置文件api的操作
 * 
 * @typeName ConfigApi
 * @date 2024年2月21日
 * @author lb
 */
@RPCClinet(name = "config", managerName = RegisterAPI.MANAGER_NAME)
public interface ConfigApi {
	/**
	 * 根据jobId 返回配置
	 * 
	 * @param jobId
	 * @return
	 */
	@API
	public Config getConfig(String jobId);

}
