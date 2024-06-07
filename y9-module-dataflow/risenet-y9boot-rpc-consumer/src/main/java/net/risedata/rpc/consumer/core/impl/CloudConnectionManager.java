package net.risedata.rpc.consumer.core.impl;


import io.netty.channel.ChannelFuture;
import net.risedata.rpc.consumer.config.ConsumerApplication;
import net.risedata.rpc.consumer.core.Connection;
import net.risedata.rpc.consumer.core.HostAndPortConnection;
import net.risedata.rpc.consumer.model.PortAndHost;
import net.risedata.rpc.consumer.task.CloudTask;
import net.risedata.rpc.consumer.task.RepetitionedTask;
import net.risedata.rpc.provide.config.Application;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import javax.sound.midi.Soundbank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 与springCloud 整合的连接管理器
 * @Author lb176
 * @Date 2021/5/6==10:20
 */
public class CloudConnectionManager extends HostAndPortConnectionManager {

    private DiscoveryClient discoveryClient;
    /**
     * 存放着 ip:端口 所对应的 server 服务端口
     */
    private static Map<String, Integer> ipAndPortMap = new ConcurrentHashMap<>();
    /**
     * 服务 连接任务的map 存放着那些服务 在任务中
     */
    private Map<String, Byte> cloudTaskMap = new HashMap<>();
    /**
     * 记录上次连接的map
     */
    private Map<String, String> connectionMap = new HashMap<>();
    /**
     * 被拿出去的连接
     */
    private Map<String, HostAndPortConnection> unEnableMap = new HashMap<>();

    public CloudConnectionManager() {
        super();
    }

    public CloudConnectionManager(ClinetBootStrap bootstrap, DiscoveryClient discovery) {
        super(null, 0, bootstrap);
        if (discovery == null) {
            throw new NullPointerException(" discovery is null");
        }
        this.discoveryClient = discovery;
    }


    public void setDiscoveryClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }


    /**
     * 注意服务需要加前缀 cloud: 连接一个服务放到此管理器的连接池
     *
     * @param serviceName 服务名
     */
    @Override
    public ChannelFuture connection(String serviceName) {
        if (!serviceName.startsWith("cloud:")) {
            return super.connection(serviceName);
        }
        Map<String, String> connectionNewMap = new HashMap<>();
        String serviceUrl = serviceName.substring(6);
        Integer port = serviceUrl.indexOf(":");
        boolean isFixedPort = false;
        int portEnd = serviceUrl.indexOf("/");
        String serviceId = port != -1 ? serviceUrl.substring(0, port) : portEnd == -1 ? serviceUrl : serviceUrl.substring(0, portEnd);
        if (port == -1) {
            port = null;
        } else {
            port = Integer.parseInt(serviceUrl.substring(port + 1, portEnd == -1 ? serviceUrl.length() : portEnd));
            isFixedPort = true;
        }

        List<ServiceInstance> clientInstances = discoveryClient.getInstances(serviceId);
        if (clientInstances.size() == 0) {
            Application.logger.warn(serviceId + " no instance");
        }

        String instanceId;
        PortAndHost portAndHost;
        outter:
        for (ServiceInstance instance : clientInstances) {
            final String id = instance.getHost() + instance.getPort();

            if (port == null) {
                port = ipAndPortMap.get(id);
                if (port == null) {
                    try {
                        port = ConsumerApplication.REST_TEMPLATE.getForObject("http://" + instance.getHost() + ":" + instance.getPort() + serviceUrl.substring(serviceId.length()) + Application.GET_PORT_URL, Integer.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("http://" + instance.getHost() + ":" + instance.getPort() + serviceUrl.substring(serviceId.length()) + Application.GET_PORT_URL);
                        continue;
                    }
                    ipAndPortMap.put(id, port);
                }
            }
            instanceId = instance.getHost() + port;
            connectionNewMap.put(instance.getInstanceId(), instanceId);

            if (cloudTaskMap.containsKey(instanceId)) {

                if (!isFixedPort) {
                    port = null;
                }
                continue;
            }

            for (HostAndPortConnection connection : this.getConnectionPool().getConnections()) {
                portAndHost = connection.getPortAndHost();

                if (instance.getHost().equals(portAndHost.getHost()) && portAndHost.getPort() == port.intValue()) {
                    if (!isFixedPort) {
                        port = null;
                    }
                    continue outter;
                }
            }
            cloudTaskMap.put(instanceId, Byte.MIN_VALUE);
            //标记这个实例已经有连接过
            //如果连接了一次就不要再次连接了
            if (unEnableMap.containsKey(instanceId)) {
                Connection connection = (Connection) unEnableMap.remove(instanceId);
                if (connection.enable()) {
                    if (!isFixedPort) {
                        port = null;
                    }
                    continue;
                }
            }
            this.connectionToHost(instance.getHost(), port).addListener((r) -> {
                if (!r.isSuccess()) {
                    //连接失败从缓存中删除port的信息除此之外会自动添加端口重连信息
                    ipAndPortMap.remove(id);
                }
            });

            if (!isFixedPort) {
                port = null;
            }

        }
        //添加定时轮询机制
        if (!cloudTaskMap.containsKey(serviceName)) {
            cloudTaskMap.put(serviceName, Byte.MIN_VALUE);
            ConsumerApplication.scheduleTask.addTask(new RepetitionedTask(10, new CloudTask(this, serviceName)));
        }
        int connectionSize = getConnectionPool().getConnections().size();
        if (connectionSize == 0) {
            Application.logger.warn(serviceName + " connection size : " + connectionSize);
        }
        //上次连接上的去这次的判断如果发现上次连接的不存在的就弃用掉连接
        for (String key : connectionMap.keySet()) {
            String id = connectionNewMap.get(key);
            if (id == null) {//为空说明这次连接的里面没有他
                instanceId = connectionMap.get(key);
                cloudTaskMap.remove(instanceId);
                ipAndPortMap.remove(instanceId);
                HostAndPortConnection[] connections = this.getConnectionPool().getConnections().toArray(new HostAndPortConnection[this.getConnectionPool().getConnections().size()]);
                for (HostAndPortConnection connection : connections) {
                    portAndHost = connection.getPortAndHost();

                    if (instanceId.equals(portAndHost.getHost() + portAndHost.getPort())) {
                        connection.abandon();
                        unEnableMap.put(instanceId, connection);
                        Application.logger.warn(key + "abandon surplus: " + connectionSize);
                    }
                }
            }
        }
        connectionMap = connectionNewMap;

        return null;
    }

}
