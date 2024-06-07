package net.risedata.rpc.consumer.config.model;


import net.risedata.rpc.consumer.config.ConsumerApplication;
import net.risedata.rpc.consumer.config.LoadingConfig;
import net.risedata.rpc.consumer.core.BasedConnectionManager;
import net.risedata.rpc.consumer.factory.ConnectionManagerFactory;
import net.risedata.rpc.exceptions.ConfigException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @description: 连接的配置用于springboot properties 自动配置
 * 注意一种连接管理器只能配置一种类型的连接
 * 配置格式为: managerName=cloud:name,rpc:ip:host
 * 如果是异步操作的连接器则为: sync:managerName = cloud:name,rpc:ip:host
 * @Author lb176
 * @Date 2021/7/30==15:19
 */
@Configuration
@ConfigurationProperties(prefix = "rpc")
public class Managers implements ApplicationListener<ApplicationReadyEvent> {
    /**
     * 配置信息
     */
    private List<String> managers;


    public List<String> getManagers() {
        return managers;
    }

    public void setManagers(List<String> managers) {
        this.managers = managers;
    }

    @Override
    public String toString() {
        return "Managers{" +
                "managers=" + managers +
                '}';
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //读取配置进行连接的操作
        if (managers == null) {
            return;
        }
        for (String manager : managers) {
            doConnection(manager.trim());
        }
    }

    private static final int LENGTH = 2;
    @Autowired
    LoadingConfig loadingConfig;

    private void doConnection(String configStr) {
        ConsumerApplication.scheduleTask.addTask(() -> {
            String[] configs = configStr.split("=");
            if (configs.length != LENGTH) {
                throw new ConfigException("unidentifiable : " + configStr);
            }
            String managerName = configs[0];
            String[] connections = configs[1].split(",");
            BasedConnectionManager connectionManager = ConnectionManagerFactory.getBasedInstance(managerName);
            if (connectionManager == null) {
                connectionManager = loadingConfig.getConnectionManager();
                ConnectionManagerFactory.registerManage(managerName, connectionManager);
            } else {
                loadingConfig.initConnectionManager(connectionManager);
            }

            for (String connection : connections) {
                connectionManager.connection(connection.trim());
            }
        });

    }
}
