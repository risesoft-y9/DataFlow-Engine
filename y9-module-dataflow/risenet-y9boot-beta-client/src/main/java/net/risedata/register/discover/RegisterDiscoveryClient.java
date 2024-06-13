package net.risedata.register.discover;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.http.HttpUtil;
import net.risedata.register.exceptions.RegisterException;
import net.risedata.register.model.RegisterServerAPI;
import net.risedata.register.rpc.RegisterAPI;
import net.risedata.register.service.IServiceInstance;
import net.risedata.rpc.consumer.core.Connection;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.factory.ConnectionManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.*;

/**
 * @Description : 服务注册于发现实现
 * @ClassName RegisterDiscoveryClinet
 * @Author lb
 * @Date 2021/11/25 17:54
 * @Version 1.0
 */
public class RegisterDiscoveryClient implements DiscoveryClient {

    @Value("${beta.discovery.environment:Public}")
    private String environment;

    @Value("${beta.discovery.serverAddr}")
    private String serversUrl ;

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterDiscoveryClient.class);

    public static final List<ServiceInstance> EMPTY = new ArrayList<>();

    private RegisterAPI registerAPI;

    public RegisterAPI getRegisterAPI() {
        return registerAPI;
    }

    public void setRegisterAPI(RegisterAPI registerAPI) {
        this.registerAPI = registerAPI;
    }

    @Override
    public String description() {
        return "Register version 1.0";
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        List<ServiceInstance> serviceInstances = DiscoveryManager.getService(serviceId.toUpperCase());

        return serviceInstances == null ? EMPTY : new ArrayList<>(serviceInstances);
    }

    @Override
    public List<String> getServices() {
        return DiscoveryManager.getServices();
    }


    public void refreshToHttp() {
        if (serversUrl == null) {
            throw new RegisterException("serversUrl is null");
        }
        JSONObject jsonObject = null;
        Set<String> keys;
        String[] servers = serversUrl.split(",");
        for (int i = 0; i < servers.length; i++) {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("environment", environment);
                jsonObject = JSON.parseObject(HttpUtil.get(servers[i] + RegisterServerAPI.GET_ALL, map, 60000));

                keys = jsonObject.keySet();
                doUpdate(keys,jsonObject);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doUpdate(Set<String> keys,JSONObject jsonObject){
        List<String> services = DiscoveryManager.getServices();
        for (String service : services) {
            if (keys.contains(service)) {
                DiscoveryManager.update(service, jsonObject.getJSONArray(service).toJavaList(IServiceInstance.class));
            } else {
                DiscoveryManager.remove(service);
            }
            keys.remove(service);
        }
        for (String key : keys) {
            DiscoveryManager.update(key, jsonObject.getJSONArray(key).toJavaList(IServiceInstance.class));
        }
    }

    private ConnectionManager connectionManager;

    /**
     * 每多久整体同步一次服务端默认60秒  单位毫秒
     */

    public void refreshAll() {

        if (registerAPI == null && serversUrl != null) {
            refreshToHttp();
        }
        if (registerAPI != null) {
            if (connectionManager == null) {
                connectionManager = ConnectionManagerFactory.getInstance(RegisterAPI.MANAGER_NAME);
            }
            if (connectionManager.getConnectionPool().size() == 0) {
                refreshToHttp();
            } else {
                registerAPI.getServices(environment).as(JSONObject.class).onSuccess((jsonObject) -> {
                    Set<String> keys = jsonObject.keySet();
                    doUpdate(keys,jsonObject);
                }).onError((req, err) -> {
                    LOGGER.error("refresh all error " + err.getMessage());
                });
            }


        }
    }

    public void compareAndSet(Connection connection) {

        connection.executionSync(RegisterServerAPI.GET_ALL, 0, environment).as(JSONObject.class).onSuccess((jsonObject) -> {
            Set<String> keys = jsonObject.keySet();
            for (String key : keys) {
                List<IServiceInstance> services = jsonObject.getJSONArray(key).toJavaList(IServiceInstance.class);
                for (IServiceInstance service : services) {
                    DiscoveryManager.register(key, service);
                }
            }
        }).onError((req, err) -> {
            LOGGER.error("compareAndSet error " + err.getMessage());
        });
    }
}
