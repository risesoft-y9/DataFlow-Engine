package net.risesoft.api.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.risedata.rpc.provide.annotation.API;
import net.risedata.rpc.provide.annotation.RPCServer;
import net.risesoft.api.persistence.config.ConfigService;
import net.risesoft.api.persistence.model.config.Config;

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

}
