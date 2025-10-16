package net.risedata.register.service;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description : 构建 自己的IServiceInstance
 * @ClassName IServiceInstanceFactory
 * @Author lb
 * @Date 2021/12/6 9:34
 * @Version 1.0
 */
public class IServiceInstanceFactory {
	
    @Autowired
    RegisterDiscoveryProperties properties;

    private volatile IServiceInstance iServiceInstance;

    public IServiceInstance getIsntance() {
        if (iServiceInstance == null) {
            synchronized (this) {
                if (iServiceInstance == null) {
                    iServiceInstance = new IServiceInstance();
                    iServiceInstance.setDescription(properties.getDescription());
                    iServiceInstance.setManagerInfo(properties.getManagerInfo());
                    iServiceInstance.setWatchInfo(properties.watchProperties);
                    iServiceInstance.setCustom(false);
                    iServiceInstance.setContext(properties.getContext());
                    iServiceInstance.setVersion(properties.getVersion());
                    iServiceInstance.setServiceId(properties.getService().toUpperCase());
                    iServiceInstance.setMetadata(properties.getMetadata());
                    iServiceInstance.setHost(properties.getIp());
                    iServiceInstance.setPort(properties.getPort());
                    iServiceInstance.setSecure(properties.isSecure());
                    iServiceInstance.setLogPath(properties.getLogPath());
                    iServiceInstance.setType(IServiceInstance.TYPE_APPLICATION);
                    iServiceInstance.setScheme(properties.isSecure() ? "https" : "http");
                    String environment = properties.getEnvironment();
                    iServiceInstance.setEnvironment(environment == null ? "Public" : environment);
                    iServiceInstance.setExpiresTime(properties.getHeartBeatTimeout());
                    iServiceInstance.setWeight(properties.getWeight());
                    iServiceInstance.setInstanceId(iServiceInstance.getServiceId() + ":" + iServiceInstance.getHost() + ":" + iServiceInstance.getPort());
                }
            }
        }
        return iServiceInstance;
    }
}
