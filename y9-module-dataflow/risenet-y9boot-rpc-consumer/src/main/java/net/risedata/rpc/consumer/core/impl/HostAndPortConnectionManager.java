package net.risedata.rpc.consumer.core.impl;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.risedata.rpc.Task.ScheduleTask;
import net.risedata.rpc.consumer.config.ConsumerApplication;
import net.risedata.rpc.consumer.config.LoadingConfig;
import net.risedata.rpc.consumer.core.Connection;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.core.ConnectionPool;
import net.risedata.rpc.consumer.core.HostAndPortConnection;
import net.risedata.rpc.consumer.exceptions.RpcException;
import net.risedata.rpc.consumer.listener.ConnectionListener;
import net.risedata.rpc.consumer.listener.impl.ConnectionPoolListener;
import net.risedata.rpc.consumer.listener.impl.LinkedConnectionListener;
import net.risedata.rpc.consumer.task.ManagerAndIpAndPortTask;
import net.risedata.rpc.utils.IdUtils;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

/**
 * @description: ip
 * @Author lb176
 * @Date 2021/5/6==10:19
 */
public class HostAndPortConnectionManager implements ConnectionManager {
    protected String host;
    protected int port;
    protected int id;
    protected ConnectionPool pool;
    protected ClientBootStrap bootstrap;
    protected LinkedConnectionListener listener;
    private ScheduleTask scheduleTask;
    private boolean isStop;

    public HostAndPortConnectionManager() {
        this.id = (int) IdUtils.getId();
        this.pool = new DefaultConnectionPool();
    }

    public HostAndPortConnectionManager(ClientBootStrap bootstrap) {
        this(null, 0, bootstrap);
    }

    public void setScheduleTask(ScheduleTask scheduleTask) {
        boolean isNull = this.scheduleTask == null;
        this.scheduleTask = scheduleTask;
        if (isNull) {
            addListener();
        }
    }

    /**
     * 创建服务器
     *
     * @param port 端口
     */
    public HostAndPortConnectionManager(String host, int port, ClientBootStrap bootStrap) {
        this();
        this.host = host;
        this.port = port;
        this.bootstrap = bootStrap;
        scheduleTask = ConsumerApplication.scheduleTask;
        iniListener();
        this.isStop = false;
    }

    public HostAndPortConnectionManager(ClientBootStrap bootstrap, ScheduleTask scheduleTask) {
        this();
        this.bootstrap = bootstrap;
        this.scheduleTask = scheduleTask;
        iniListener();
    }

    public void setBootstrap(ClientBootStrap bootstrap) {
        this.bootstrap = bootstrap;
        iniListener();
    }

    private void iniListener() {
        if (listener == null) {
            listener = new LinkedConnectionListener();
            if (scheduleTask != null) {
                addListener();
            }
        }
    }

    private void addListener() {
        listener.addListener(new ConnectionPoolListener(pool, this, scheduleTask));
        if (LoadingConfig.connectionListener != null) {
            listener.addListener(LoadingConfig.connectionListener);
        }
    }


    @Override
    public Connection getConnection() {
        return (Connection) pool.getConnection();
    }

    @Override
    public ChannelFuture connection(String url) {
        //特殊的识别
        try {
            String[] split = url.split(":");
            return connectionToHost(split[1], Integer.valueOf(split[2]));
        } catch (Exception e) {
            throw new RpcException("connection url error:" + url);
        }
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void connection() {
        if (this.host == null || this.port == 0) {
            throw new NullPointerException("port or host is null");
        }
        try {
            ChannelFuture await = connectionToHost(this.host, this.port).sync().await();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChannelFuture connectionToHost(String host, int port) {

        if (this.isStop) {
            return null;
        }
        ChannelConnection channelConnection = new ChannelConnection(host, port, pool, this);
        channelConnection.setListener(this.listener);
        ConnectionManager connectionManager = this;
        try {
            return bootstrap.connection(host, port, channelConnection).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {

                    if (!channelFuture.isSuccess()) {
                        scheduleTask.addTask(new ManagerAndIpAndPortTask(connectionManager, host, port));
                    }
                }
            });
        } catch (InterruptedException e) {
            return null;
        } catch (RejectedExecutionException e){
            return null;
        }
    }

    @Override
    public boolean connectionAwait(String host, int port) {
        try {
            return connectionToHost(host, port).await().isSuccess();
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public void addListener(ConnectionListener listener) {
        this.listener.addListener(listener);
    }

    @Override
    public void close() {
        List<Connection> connections = pool.getConnections();
        for (HostAndPortConnection connection : connections) {
            connection.close();
        }
    }

    @Override
    public ConnectionPool getConnectionPool() {
        return pool;
    }

    @Override
    public void stop() {
        this.isStop = true;
    }
}
