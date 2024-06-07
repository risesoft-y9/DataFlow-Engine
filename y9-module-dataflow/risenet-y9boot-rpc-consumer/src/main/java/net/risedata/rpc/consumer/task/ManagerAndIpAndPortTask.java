package net.risedata.rpc.consumer.task;

import net.risedata.rpc.Task.Task;
import net.risedata.rpc.consumer.config.ConsumerApplication;
import net.risedata.rpc.consumer.config.LoadingConfig;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.provide.config.Application;

/**
 * @description: 对指定的连接管理器执行指定的ip和host重连的任务
 * @Author lb176
 * @Date 2021/4/29==15:18
 */
public class ManagerAndIpAndPortTask implements Task {
    private ConnectionManager connectionManager;
    private String host;
    private int port;


    public ManagerAndIpAndPortTask(ConnectionManager connectionManager, String host, int port) {
        this.connectionManager = connectionManager;
        this.host = host;
        this.port = port;
    }


    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


    @Override
    public void run() {
        if (ConsumerApplication.CLINET_CONFIG.showTaskInfo) {
            Application.logger.info("正在重连 host=" + host + "port " + port);
        }
        connectionManager.connectionToHost(host, port);
    }
}
