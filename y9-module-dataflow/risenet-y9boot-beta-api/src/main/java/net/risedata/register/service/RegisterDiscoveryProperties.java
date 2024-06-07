package net.risedata.register.service;

import com.alibaba.fastjson.JSONObject;

import net.risedata.register.model.WatchProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("beta.discovery")
public class RegisterDiscoveryProperties implements InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(RegisterDiscoveryProperties.class);

	/**
	 * service name to registry.
	 */
	@Value("${beta.discovery.service:${spring.application.name:}}")
	private String service;

	/**
	 * 实例版本
	 */
	private String version;

	/**
	 * 实例描述
	 */
	private String description;

	/**
	 * 负责人信息
	 */
	private String managerInfo;
	/**
	 * 日志文件路径
	 */
	private String logPath;
	/**
	 * 上下文
	 */
	private String context;
	/**
	 * extra metadata to register.
	 */
	private Map<String, String> metadata = new HashMap<>();
	/**
	 * 权重
	 */
	private int weight;
	/**
	 * 如果只是订阅而不注册则为false
	 */
	private boolean registerEnabled = true;

	/**
	 * 指定ip
	 */
	private String ip;
	/**
	 * 指定环境
	 */
	private String environment;

	/**
	 * which network interface's ip you want to register.
	 */
	private String networkInterface = "";

	/**
	 * 哪个端口提供服务 默认为 自动
	 */
	private int port = -1;

	/**
	 * 是否为https
	 */
	private boolean secure = false;

	/**
	 * 心跳时间 定时任务 默认30秒一次
	 */
	private Integer heartBeatInterval = 30000;

	/**
	 * 多少秒没反应则视为服务停止则设置状态后移除服务 默认 90秒
	 */

	private Integer heartBeatTimeout = 90000;

	@Autowired(required = false)
	private InetUtils inetUtils;

	@Autowired
	private Environment environmentUtil;

	@Autowired(required = false)
	WatchProperties watchProperties;

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public void init() throws SocketException {

		if (secure) {
			metadata.put("secure", "true");
		}

//        if (watchProperties!=null&&watchProperties.isWatch()){
//            metadata.put(WatchProperties.WATCHKEY, JSONObject.toJSONString(watchProperties));
//        }
		if (StringUtils.isEmpty(environment)) {
			environment = "Public";
		}
		if (StringUtils.isEmpty(ip)) {
			// traversing network interfaces if didn't specify a interface
			if (StringUtils.isEmpty(networkInterface)) {
				if (inetUtils != null) {

					ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
				} else {
					try {
						ip = InetAddress.getLocalHost().getHostAddress();
					} catch (UnknownHostException e) {
						throw new IllegalArgumentException("no such interface " + e.getMessage());
					}
				}
			} else {
				NetworkInterface netInterface = NetworkInterface.getByName(networkInterface);
				if (null == netInterface) {
					throw new IllegalArgumentException("no such interface " + networkInterface);
				}

				Enumeration<InetAddress> inetAddress = netInterface.getInetAddresses();
				while (inetAddress.hasMoreElements()) {
					InetAddress currentAddress = inetAddress.nextElement();
					if (currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress()) {
						ip = currentAddress.getHostAddress();
						break;
					}
				}

				if (StringUtils.isEmpty(ip)) {
					throw new RuntimeException(
							"cannot find available ip from" + " network interface " + networkInterface);
				}

			}
		}

		this.overrideFromEnv(environmentUtil);
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getManagerInfo() {
		return managerInfo;
	}

	public void setManagerInfo(String managerInfo) {
		this.managerInfo = managerInfo;
	}

	public void setInetUtils(InetUtils inetUtils) {
		this.inetUtils = inetUtils;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public boolean isRegisterEnabled() {
		return registerEnabled;
	}

	public void setRegisterEnabled(boolean registerEnabled) {
		this.registerEnabled = registerEnabled;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNetworkInterface() {
		return networkInterface;
	}

	public void setNetworkInterface(String networkInterface) {
		this.networkInterface = networkInterface;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public Integer getHeartBeatInterval() {
		return heartBeatInterval;
	}

	public void setHeartBeatInterval(Integer heartBeatInterval) {
		this.heartBeatInterval = heartBeatInterval;
	}

	public Integer getHeartBeatTimeout() {
		return heartBeatTimeout;
	}

	public void setHeartBeatTimeout(Integer heartBeatTimeout) {
		this.heartBeatTimeout = heartBeatTimeout;
	}

	public void overrideFromEnv(Environment env) {

		/*
		 * if (StringUtils.isEmpty(this.getServerAddr())) { this.setServerAddr(env
		 * .resolvePlaceholders("${register.discovery.server-addr:}")); }
		 */

		if (this.getPort() == -1) {
			this.setPort(Integer.parseInt(env.resolvePlaceholders("${server.port}")));
		}
		log.debug(this.toString());
	}

	@Override
	public String toString() {
		return "RegisterDiscoveryProperties{" + "service='" + service + '\'' + ", version='" + version + '\''
				+ ", description='" + description + '\'' + ", managerInfo='" + managerInfo + '\'' + ", metadata="
				+ metadata + ", registerEnabled=" + registerEnabled + ", ip='" + ip + '\'' + ", networkInterface='"
				+ networkInterface + '\'' + ", port=" + port + ", secure=" + secure + ", heartBeatInterval="
				+ heartBeatInterval + ", heartBeatTimeout=" + heartBeatTimeout + ", watchProperties=" + watchProperties
				+ '}';
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.init();

	}
}