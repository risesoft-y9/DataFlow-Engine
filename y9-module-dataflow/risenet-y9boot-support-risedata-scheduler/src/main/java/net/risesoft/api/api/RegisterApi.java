package net.risesoft.api.api;

import net.risedata.register.service.IServiceInstance;
import net.risedata.rpc.consumer.annotation.Listeners;
import net.risedata.rpc.provide.annotation.API;
import net.risedata.rpc.provide.annotation.Param;
import net.risedata.rpc.provide.annotation.RPCServer;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risesoft.api.exceptions.RegisterException;
import net.risesoft.api.persistence.iservice.IServiceService;
import net.risesoft.api.persistence.model.IServiceInstanceModel;
import net.risesoft.api.watch.WatchManager;
import net.risesoft.security.model.Environment;
import net.risesoft.security.model.NetworkWhiteList;
import net.risesoft.security.SecurityManager;
import net.risesoft.security.dao.EnvironmentDao;
import net.risesoft.security.service.EnvironmentService;
import net.risesoft.security.service.NetworkWhiteListService;
import net.risesoft.util.PattenUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * 
 * @Description : 提供注册服务
 * @ClassName api
 * @Author lb
 * @Date 2021/11/25 16:04
 * @Version 1.0
 */

@Listeners
@RestController
@RPCServer(name = "register", enableRequest = true)
public class RegisterApi {
	/**
	 * 存在连接中的信息id
	 */
	public static final String CONNECTION_INSTANCE_ID = "CONNECTION_INSTANCE_ID";
	/**
	 * 存在连接中的信息name
	 */
	public static final String CONNECTION_INSTANCE_NAME = "CONNECTION_INSTANCE_NAME";
	/**
	 * 存在连接中的信息环境信息environment
	 */
	public static final String INSTANCE_ENVIRONMENT_NAME = "INSTANCE_ENVIRONMENT_NAME";

	/**
	 * 监听删除服务
	 */
	public static final String REMOVED_ALL_REGISTER = "REGISTER_REMOVED_ALL_REGISTER";

	@Autowired
	WatchManager watchManager;

	public static final List<IServiceInstance> EMPTY = new ArrayList<>();

	@Value("${beta.discovery.service:${spring.application.name:}}")
	String MYSERVICENAME;

	/**
	 * 获取所有实例
	 *
	 * @return
	 */
	@API
	public Map<String, List<IServiceInstanceModel>> getServices(
			@RequestParam(required = false, defaultValue = "Public") @Param(required = false, defaultValue = "Public") String environment) {
		return iServiceService.getUseAll(environment);
	}

	@Autowired
	SecurityManager securityManager;

	@Autowired
	NetworkWhiteListService networkWhiteListService;
	
	@Autowired
	private EnvironmentDao environmentDao;

	private boolean check(String environment, IServiceInstanceModel iServiceInstance, String ip) {
		return checkeOfIp(environment, ip, iServiceInstance);

	}

	private boolean checkeOfIp(String environment, String ip, IServiceInstanceModel iServiceInstance) {
		List<NetworkWhiteList> networkWhiteList = networkWhiteListService.getNetworkWhiteList(environment);
		if (networkWhiteList == null || networkWhiteList.size() == 0) {
			return true;
		}
		for (NetworkWhiteList whiteList : networkWhiteList) {
			if (PattenUtil.hasMatch(networkWhiteListService.getStrs(whiteList.getIpMatch()), ip) && PattenUtil.hasMatch(
					networkWhiteListService.getStrs(whiteList.getService()), iServiceInstance.getServiceId())) {
				return true;
			}
		}
		return false;
	}

	@Autowired(required = false)
	IServiceService iServiceService;

	@Autowired
	EnvironmentService environmentService;

	public boolean register(IServiceInstanceModel serviceInstance) {
		if (StringUtils.isEmpty(serviceInstance.getEnvironment())) {
			serviceInstance.setEnvironment(Environment.PUBLIC);
		}
		serviceInstance.setUpdateTime(System.currentTimeMillis());
		if (serviceInstance.getRegisterTime() == null || serviceInstance.getRegisterTime() == 0) {
			serviceInstance.setRegisterTime(System.currentTimeMillis());
		}
		
		if (environmentDao.hasPublic() == 0) {
			environmentDao.create("Public", "Public", "默认环境");
			environmentDao.create("dev", "dev", "测试环境");
		}
		
		environmentService.getEnvironmentByName(serviceInstance.getEnvironment());
		return iServiceService.saveModel(serviceInstance);
	}

	/**
	 * 注册服务到中心
	 *
	 * @param serviceInstance 实例
	 * @return
	 */
	@API(name = "register")
	public boolean registerToServer(IServiceInstanceModel serviceInstance, RPCRequestContext rpcRequestContext) {
		if (StringUtils.isEmpty(serviceInstance.getHost())) {
			serviceInstance.setHost(((InetSocketAddress) rpcRequestContext.getConcurrentConnection().getRemoteAddress())
					.getHostString());
		}
		String ip = securityManager.getConcurrentIp();
		if (StringUtils.isBlank(serviceInstance.getEnvironment())) {
			serviceInstance.setEnvironment(Environment.PUBLIC);
		}
		if (!check(serviceInstance.getEnvironment(), serviceInstance, ip)) {
			throw new RegisterException("no security");
		}
		serviceInstance.setCustom(false);
		
		rpcRequestContext.getConcurrentConnection().setAttribute(INSTANCE_ENVIRONMENT_NAME,
				serviceInstance.getEnvironment());
		rpcRequestContext.getConcurrentConnection().setAttribute(CONNECTION_INSTANCE_NAME,
				serviceInstance.getServiceId());
		rpcRequestContext.getConcurrentConnection().setAttribute(CONNECTION_INSTANCE_ID,
				serviceInstance.getInstanceId());
		return register(serviceInstance);
	}

	@API
	public boolean remove(String name, String instanceId) {
		return iServiceService.delById(instanceId);
	}

	/**
	 * 续订
	 *
	 * @param rpcRequestContext
	 * @return
	 */
	@API
	public boolean reNew(RPCRequestContext rpcRequestContext) {
		Object instanceId = rpcRequestContext.getConcurrentConnection().getAttribute(CONNECTION_INSTANCE_ID);
		if (instanceId != null) {
			return iServiceService.updateNowTime((String) instanceId) > 0;
		} else {
			throw new RegisterException("no instance id");
		}
	}

	/**
	 * 获取所有服务
	 * 
	 * @param environment
	 * @return
	 */
	@RequestMapping("/getServices")
	public Map<String, List<IServiceInstanceModel>> getServicesAll(
			@RequestParam(required = false, defaultValue = "Public") String environment) {
		return iServiceService.getUseAll(environment);
	}
}
