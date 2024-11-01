package net.risedata.rpc.consumer.config;

import net.risedata.rpc.Task.Task;
import net.risedata.rpc.consumer.annotation.API;
import net.risedata.rpc.consumer.annotation.ManagerListener;
import net.risedata.rpc.consumer.annotation.RPCClient;
import net.risedata.rpc.consumer.core.BasedConnectionManager;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.core.impl.ClientBootStrap;
import net.risedata.rpc.consumer.core.impl.CloudConnectionManager;
import net.risedata.rpc.consumer.core.impl.HostAndPortConnectionManager;
import net.risedata.rpc.consumer.factory.ClientBeanFactory;
import net.risedata.rpc.consumer.factory.ConnectionManagerFactory;
import net.risedata.rpc.consumer.factory.ReturnValueHandleFactory;
import net.risedata.rpc.consumer.invoke.InvokeHandle;
import net.risedata.rpc.consumer.invoke.ProxyInvoke;
import net.risedata.rpc.consumer.listener.*;
import net.risedata.rpc.consumer.listener.impl.LinkedConnectionListener;
import net.risedata.rpc.consumer.model.APIDescription;
import net.risedata.rpc.consumer.result.SyncResult;
import net.risedata.rpc.consumer.utils.ConsumerUtils;
import net.risedata.rpc.exceptions.ConfigException;
import net.risedata.rpc.factory.ReturnTypeFactory;
import net.risedata.rpc.provide.config.Application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 加载的config
 * @Author lb176
 * @Date 2021/4/29==15:20
 */
@Order(value = 2)
public class LoadingConfig implements  ApplicationListener<ContextRefreshedEvent> {

    /**
     * 客户端启动器
     */
    @Autowired
    private ClientBootStrap clientBootStrap;
    /**
     * 超时时间默认是1秒
     */
    @Value("${rpc.timeOut:1000}")
    private int timeOut;
    /**
     * 多少个线程数量
     */
    @Value("${rpc.threadSize:10}")
    private int size;
    /**
     * 非 spring cloud 环境下 则不需要
     */
    public static boolean isDiscoveryClient;

    @Autowired(required = false)
    private DiscoveryClient discoveryClient;

    @Autowired
    ConnectionConfig connectionConfig;

    public static ConnectionListener connectionListener;

    private boolean isInit;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (event.getApplicationContext().getParent() == null || event.getApplicationContext().getParent().getParent() == null) {
            if (isInit){
                return;
            }

            isInit = true;
            refreshListener(event.getApplicationContext(),true);
            Application.logger.info(" cloud " + (discoveryClient != null));
            ClientBeanFactory.invokeMap.forEach(this::registerClinet);
            ConsumerApplication.scheduleTask.addTask(() -> {
                connectionConfig.getConfigs().forEach((k, v) -> {
                    BasedConnectionManager connectionManager = ConnectionManagerFactory.getInstance(k);
                    if (connectionManager == null) {
                        connectionManager = getConnectionManager();
                        ConnectionManagerFactory.registerManage(k, connectionManager);
                    }
                    initConnectionManager(connectionManager);
                    for (String url : v) {
                        connectionManager.connection(url);
                    }

                });
            });
            refreshListener(event.getApplicationContext(),false);
            Map<String, RPCStartListener> listeners = event.getApplicationContext().getBeansOfType(RPCStartListener.class);
            if (listeners != null && listeners.size() > 0) {
                for (RPCStartListener value : listeners.values()) {
                    value.run();
                }
            }
            Application.logger.info(" threadSize " + size);
            clientBootStrap.start(size);
            ConsumerApplication.scheduleTask.start();
        }
    }


    public ConnectionManager getConnectionManager() {
        if (isDiscoveryClient) {
            return new CloudConnectionManager(clientBootStrap, discoveryClient);
        } else {
            return new HostAndPortConnectionManager(clientBootStrap);
        }
    }


    public static ConnectionManager getNullConnectionManager() {
        if (isDiscoveryClient) {
            return new CloudConnectionManager();
        } else {
            return new HostAndPortConnectionManager();
        }
    }


    /**
     * 将 此客户端接口注册到 连接池中
     *
     * @param type  类型
     * @param proxy 代理
     */
    private void registerClinet(Class<?> type, ProxyInvoke proxy) {

        RPCClient rpcClinet = AnnotationUtils.findAnnotation(type, RPCClient.class);

        if (rpcClinet != null) {
            String[] addrs = rpcClinet.value();
            String apiName = ConsumerUtils.getManagerName(type, rpcClinet);

            Map<Method, APIDescription> methodMap = new HashMap<>();
            //接口中都是public 既然代理接口则应为public
            Method[] methods = type.getMethods();
            API api;
            String n;
            APIDescription apiDescription;
            ConnectionManager connectionManager = ConnectionManagerFactory.getInstance(apiName);
            initConnectionManager(connectionManager);
            proxy.setConnectionManager(connectionManager);

            for (Method method : methods) {
                api = AnnotationUtils.findAnnotation(method, API.class);
                if (api != null && !StringUtils.isEmpty(api.name())) {
                    n = ConsumerUtils.getValue(api.name());
                } else {
                    n = method.getName();
                }
                apiDescription = new APIDescription(
                        (api == null || api.timeOut() < 0) ?
                                rpcClinet.timeOut() < 0 ?
                                        timeOut
                                        : rpcClinet.timeOut()
                                : api.timeOut(), ConsumerUtils.getValue(rpcClinet.name()) + "/" + n);
                apiDescription.setReturnType(ReturnTypeFactory.parseInstance(method));
                apiDescription.setReturnValueHandle(ReturnValueHandleFactory.getInstance(apiDescription.getReturnType()));

                if (apiDescription.getReturnValueHandle() == null) {
                    throw new ConfigException(method + " 无法识别的类型");
                }
                if (method.getReturnType() == SyncResult.class) {
                    apiDescription.setSync(true);
                }
                if (api != null && api.invokeHandle() != InvokeHandle.class) {
                    apiDescription.setInvokeHandle(ConsumerApplication.applicationContext.getBean(api.invokeHandle()));
                } else {
                    apiDescription.setInvokeHandle(proxy);
                }
                methodMap.put(method, apiDescription);
            }
            proxy.setApiMap(methodMap);

            if (rpcClinet.degrade() != ExceptionListener.class) {
                proxy.setFusing(ConsumerApplication.applicationContext.getBean(rpcClinet.degrade().getName()));
            }
            if (rpcClinet.exceptionHandle() != ExceptionListener.class) {
                proxy.setExceptionListener(ConsumerApplication.applicationContext.getBean(rpcClinet.exceptionHandle()));
            }
            ConsumerApplication.scheduleTask.addTask(new Task() {
                @Override
                public void run() {
                    String[] adds = null;
                    for (String addr : addrs) {
                        adds = ConsumerUtils.getValue(addr).split(",");
                        for (String connectionUrl : adds) {
                            try {
                                connectionManager.connection(connectionUrl);
                            } catch (Exception e) {

                            }
                        }
                    }
                }
            });
        }
    }

    public void initConnectionManager(BasedConnectionManager connectionManager) {
        if (connectionManager instanceof HostAndPortConnectionManager) {
            HostAndPortConnectionManager hostManager = ((HostAndPortConnectionManager) connectionManager);
            hostManager.setBootstrap(clientBootStrap);
            hostManager.setScheduleTask(ConsumerApplication.scheduleTask);
        }
        if (connectionManager instanceof CloudConnectionManager) {
            CloudConnectionManager cloudManager = ((CloudConnectionManager) connectionManager);
            cloudManager.setDiscoveryClient(discoveryClient);
        }
    }


    private void refreshListener(ApplicationContext applicationContext,boolean isApplication) {
        Map<String, ConnectionListener> connectionListeners = applicationContext.getBeansOfType(ConnectionListener.class);
        LinkedConnectionListener linkedConnectionListener = new LinkedConnectionListener();
        if (connectionListeners.size() > 0) {

            Collection<ConnectionListener> connectionListenerss = connectionListeners.values();
            ManagerListener managerListener = null;
            ConnectionManager connectionManager = null;
            String[] names = null;
            Class<?>[] classes = null;
            for (ConnectionListener listener :
                    connectionListenerss) {
                managerListener = AnnotationUtils.findAnnotation(listener.getClass(), ManagerListener.class);
                if (managerListener == null) {
                    if (isApplication){

                        linkedConnectionListener.addListener(listener);
                    }
                } else {
                    if(isApplication){
                        continue;
                    }

                    names = managerListener.name();
                    classes = managerListener.classes();
                    for (String name : names) {
                        connectionManager = ConnectionManagerFactory.getInstance(name);
                        if (connectionManager == null) {
                            throw new NullPointerException(name + " connection manager is null");
                        }
                        connectionManager.addListener(listener);
                    }
                    String name = null;
                    for (Class<?> aClass : classes) {
                        RPCClient rpcClinet = AnnotationUtils.findAnnotation(aClass, RPCClient.class);
                        name = ConsumerUtils.getManagerName(aClass, rpcClinet);
                        connectionManager = ConnectionManagerFactory.getInstance(name);
                        if (connectionManager == null) {
                            throw new NullPointerException(name + " connection manager is null");
                        }
                        connectionManager.addListener(listener);
                    }
                }
            }
            connectionListener = linkedConnectionListener;
        }
    }
}
