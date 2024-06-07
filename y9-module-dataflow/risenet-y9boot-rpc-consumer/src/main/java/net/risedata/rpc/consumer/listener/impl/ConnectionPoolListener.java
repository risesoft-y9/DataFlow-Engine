package net.risedata.rpc.consumer.listener.impl;

import net.risedata.rpc.Task.ScheduleTask;
import net.risedata.rpc.consumer.core.Connection;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.core.ConnectionPool;
import net.risedata.rpc.consumer.core.HostAndPortConnection;
import net.risedata.rpc.consumer.core.impl.ChannelConnection;
import net.risedata.rpc.consumer.listener.ConnectionListener;
import net.risedata.rpc.consumer.model.PortAndHost;
import net.risedata.rpc.consumer.task.ManagerAndIpAndPortTask;
import net.risedata.rpc.provide.config.Application;


/**
 * 主要管理连接池和 连接直接的问题的监听器
 */
public class ConnectionPoolListener implements ConnectionListener {
    //增加 监听 如果pool为0 了则创建链接
    private ConnectionPool pool;
    private ScheduleTask task;
    private ConnectionManager connectionManager;
    private ConnectionListener connectionListener;

    public ConnectionPoolListener(ConnectionPool pool, ConnectionManager connectionManager, ScheduleTask task) {

        this.pool = pool;
        this.task = task;
        this.connectionManager = connectionManager;
    }

    public ConnectionPoolListener(ConnectionPool pool, ScheduleTask task, ConnectionManager connectionManager, ConnectionListener connectionListener) {
        this.pool = pool;
        this.task = task;
        this.connectionManager = connectionManager;
        this.connectionListener = connectionListener;
    }

    @Override
    public void onConnection(HostAndPortConnection connection) {
        pool.addConnection((Connection) connection);
        if (connectionListener != null) {
            connectionListener.onConnection(connection);
        }
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    @Override
    public void onClose(HostAndPortConnection connection) {

            PortAndHost portAndHost = connection.getPortAndHost();
            if (this.pool.removeConnection((Connection) connection)) {
                Application.logger.info("connection " + portAndHost + " is closed  remain : " + pool.size());
                if (connectionListener != null) {
                    connectionListener.onClose(connection);
                }
            }
        if(connection.isUse()){
            task.addTask(new ManagerAndIpAndPortTask(connectionManager, portAndHost.getHost(), portAndHost.getPort()));
        }
    }
}
