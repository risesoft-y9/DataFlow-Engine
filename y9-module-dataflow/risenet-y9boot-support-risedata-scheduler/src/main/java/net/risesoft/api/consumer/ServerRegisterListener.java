package net.risesoft.api.consumer;

import com.alibaba.fastjson.JSONObject;

import net.risedata.register.model.Const;
import net.risedata.register.model.RegisterServerAPI;
import net.risedata.register.rpc.RegisterAPI;
import net.risedata.register.service.IServiceInstance;
import net.risedata.rpc.consumer.annotation.Listener;
import net.risedata.rpc.consumer.annotation.Listeners;
import net.risedata.rpc.consumer.annotation.ManagerListener;
import net.risedata.rpc.consumer.core.Connection;
import net.risedata.rpc.consumer.core.HostAndPortConnection;
import net.risedata.rpc.consumer.listener.ConnectionListener;
import net.risedata.rpc.provide.net.ClinetConnection;
import net.risedata.rpc.service.RPCExecutorService;
import net.risesoft.api.api.RegisterApi;
import net.risesoft.api.config.model.ServiceConfigModel;
import net.risesoft.api.listener.ClientListener;
import net.risesoft.api.persistence.model.IServiceInstanceModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description : 监听客户端与服务端连接的建立 此处是与其他RpcServer 进行建立连接用于服务的同步监控与发现
 * @ClassName Listener
 * @Author lb
 * @Date 2021/11/26 14:59
 * @Version 1.0
 */
@Listeners
@ManagerListener(classes = RegisterAPI.class)
public class ServerRegisterListener implements ConnectionListener {

    private static final Logger log = LoggerFactory
            .getLogger(ServerRegisterListener.class);

    @Autowired
    RPCExecutorService executorService;

    @Override
    public void onConnection(HostAndPortConnection connection) {

    }


    @Override
    public void onClose(HostAndPortConnection connection) {

    }

    @Listener(value = Const.REMOVE_LISTENER)
    public void remove(String serviceId, String instanceId, String environment, Integer level) {
        if (level == null || level == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("serviceId", serviceId);
            map.put("instanceId", instanceId);
            map.put("environment", environment);
            map.put("level", 1);

            ClientListener.pushListener(Const.REMOVE_LISTENER, map, serviceConfigModel.getMyService(), null, environment);
        }
    }

    @Autowired
    ServiceConfigModel serviceConfigModel;

    @Listener(value = Const.REGISTER_LISTENER)
    public void register(IServiceInstanceModel instance, String environment, Integer level) {
        if (level == null || level == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("instance", instance);
            map.put("environment", environment);
            map.put("level", 1);
            ClientListener.pushListener(Const.REGISTER_LISTENER, map, serviceConfigModel.getMyService(), null, environment);
        }

    }


    @Listener(value = Const.CHANGE_LISTENER)
    public void change(IServiceInstance instance, Integer status) {

        //根据状态广播

    }


}
