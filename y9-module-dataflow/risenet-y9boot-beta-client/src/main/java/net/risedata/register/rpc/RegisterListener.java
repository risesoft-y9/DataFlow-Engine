package net.risedata.register.rpc;

import net.risedata.register.discover.DiscoveryManager;
import net.risedata.register.discover.RegisterDiscoveryClient;
import net.risedata.register.model.Const;
import net.risedata.register.model.WatchProperties;
import net.risedata.register.service.IServiceInstance;
import net.risedata.register.service.IServiceInstanceFactory;
import net.risedata.register.service.RegisterDiscoveryProperties;
import net.risedata.rpc.consumer.annotation.Listener;
import net.risedata.rpc.consumer.annotation.Listeners;
import net.risedata.rpc.consumer.annotation.ManagerListener;
import net.risedata.rpc.consumer.core.Connection;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.core.ConnectionPool;
import net.risedata.rpc.consumer.core.HostAndPortConnection;
import net.risedata.rpc.consumer.core.impl.ChannelConnection;
import net.risedata.rpc.consumer.listener.ConnectionListener;
import net.risedata.rpc.consumer.listener.ListenerBack;
import net.risedata.rpc.consumer.model.ListenerRequest;
import net.risedata.rpc.consumer.model.PortAndHost;
import net.risedata.rpc.model.ListenerResponse;
import net.risedata.rpc.model.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * @Description : 用来监听于服务器的连接
 * @ClassName Listener
 * @Author lb
 * @Date 2021/11/26 14:59
 * @Version 1.0
 */
@Listeners
@ManagerListener(classes = RegisterAPI.class)
public class RegisterListener implements ConnectionListener, ListenerBack {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterListener.class);

    @Autowired
    RegisterDiscoveryProperties registerDiscoveryProperties;

    @Resource(name = RegisterAPI.MANAGER_NAME)
    ConnectionManager connectionManager;

    @Autowired
    IServiceInstanceFactory iServiceInstanceFactory;

    @Autowired
    WatchProperties wa;
    
    @Autowired
    RegisterAPI registerAPI;

    @Autowired
    RegisterDiscoveryClient registerDiscoveryClient;

    @Scheduled(fixedDelayString = "${beta.discovery.refreshAll:60000}", initialDelayString = "${beta.discovery.refreshAll:60000}")
    public void refreshAll() {
        if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("refresh all");
        }
        registerDiscoveryClient.refreshAll();
    }

    @Scheduled(fixedDelayString = "${beta.discovery.heartBeat:30000}", initialDelayString = "${beta.discovery.heartBeat:30000}")
    public void reNewTime() {
        if (registerDiscoveryClient.getRegisterAPI() != null && connectionManager.getConnectionPool().size() > 0) {
            registerAPI.reNew().as(Boolean.class).onSuccess((res) -> {
                if (!res) {
                    IServiceInstance instance = iServiceInstanceFactory.getIsntance();
                    if (LOGGER.isDebugEnabled()) {
                    	LOGGER.debug("re new time : " + instance);
                    }
                    registerAPI.register(instance).as(Boolean.class).onSuccess((res2) -> {
                        if (LOGGER.isDebugEnabled()) {
                        	LOGGER.debug("register result " + res2);
                        }
                    });
                }
            }).onError((req, err) -> {
            	LOGGER.error("reNewTime error " + err.getMessage());
            });
        }
    }

    @Override
    public void onConnection(HostAndPortConnection connection) {
        IServiceInstance instance = iServiceInstanceFactory.getIsntance();
        if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("register : " + instance);
        }
        ((ChannelConnection) connection).executionSync("register/register", 5000, instance).as(Boolean.class).onSuccess((res) -> {
            if (LOGGER.isDebugEnabled()) {
            	LOGGER.debug("register result " + res);
            }
            if (registerDiscoveryClient.getRegisterAPI() == null) {
            	registerDiscoveryClient.setRegisterAPI(registerAPI);
            }
            if (connectionManager.getConnectionPool().size() == 1) {
            	registerDiscoveryClient.refreshAll();
            } else {
            	registerDiscoveryClient.compareAndSet((Connection) connection);
            }
        }).onError((res, e) -> {
            LOGGER.error("register error " + e.getMessage());
        });
    }

    public static String getWatchStr(PortAndHost portAndHost) {
        return portAndHost.getHost() + ":" + portAndHost.getPort();
    }

    @Override
    public void onClose(HostAndPortConnection connection) {
        //ChannelConnection connection1 = (ChannelConnection) connection;
        if (!wa.isWatch()) {
            return;
        }
    }

    @Listener(value = Const.REMOVE_LISTENER)
    public void remove(String serviceId, String instanceId) {
        DiscoveryManager.remove(serviceId, instanceId);
    }

    @Listener(value = Const.REGISTER_LISTENER)
    public void register(IServiceInstance instance) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(instance + " 注册");
        }
        DiscoveryManager.register(instance.getServiceId(), instance);
    }

    @Listener(value = Const.CHANGE_LISTENER)
    public void change(IServiceInstance instance, Integer status) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(instance + " status " + status);
        }
        if (status != IServiceInstance.SUCCESS) {
            DiscoveryManager.remove(instance.getServiceId(), instance.getInstanceId());
        } else {
            DiscoveryManager.register(instance.getServiceId(), instance);
        }
    }

    @Resource
    JobAPI jobAPI;
    
    @Override
    public void onBackError(ListenerRequest request, ListenerResponse response, ChannelConnection channelConnection) {
        //是否为任务调度出现的问题
        if (request.getArgs()!=null&&request.getArgs().containsKey("jobId")&&request.getArgs().containsKey("jobLogId")){
            LOGGER.info("任务调度服务器连接异常提交api");
            Integer jobId =request.getArgs().getInteger("jobId");
            String jobLogId = request.getArgs().getString("jobLogId");
            ConnectionPool connectionPool = channelConnection.getConnectionManager().getConnectionPool();
            for (Connection connection : connectionPool.getConnections()) {
                if(!connection.isRemoved()){

                    jobAPI.endJob(jobId,jobLogId,response.getMsg(),response.getMsg(),response.getStatus()== Response.SUCCESS?1:2).as(Boolean.class).onSuccess((b)->{
                        if (b){
                            LOGGER.info("任务调度服务器连接异常提交api成功");
                        }else{
                            LOGGER.error("任务确认失败");
                        }
                    }).onError((req,e)->{
                        LOGGER.error("任务确认失败："+e.getMessage());
                        e.printStackTrace();
                    });
                    return;
                }
            }
            throw new RuntimeException("任务确认失败");
        }
    }
}
