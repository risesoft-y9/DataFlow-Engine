package net.risedata.rpc.consumer.core.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.risedata.rpc.consumer.core.Connection;
import net.risedata.rpc.consumer.core.ConnectionPool;
import net.risedata.rpc.consumer.exceptions.RpcException;
import net.risedata.rpc.consumer.utils.NoNullArrayList;

/**
 * @description: 默认的连接池
 * @Author lb176
 * @Date 2021/4/29==14:54
 */
class DefaultConnectionPool implements ConnectionPool {
    /**
     * 连接的集合
     */
    private List<Connection> connections = new NoNullArrayList();
    /**
     * 当前拿到的index
     */
    private AtomicInteger index = new AtomicInteger(0);


    private int getIndex() {
        int concurrent, i;
        for (; ; ) {
            concurrent = index.get();
            i = ((concurrent + 1) % connections.size());
            if (index.compareAndSet(concurrent, i)) {
                return i;
            }
        }
    }

    @Override
    public void addConnection(Connection connection) {
        if (connection == null) {
            throw new RpcException("null connection");
        }
        connections.add(connection);
    }

    @Override
    public synchronized boolean removeConnection(Connection connection) {
        return connections.remove(connection);
    }

    @Override
    public Connection getConnection() {
        //TODO 负载均衡策略 当前是轮询 应使用获取请求数堆积较少的来进行请求
//       return getIndexOfAction();
        return connections.get(getIndex());
    }


    /**
     * 根据请求数据获取连接
     * @return
     */
    public Connection getIndexOfAction() {
        int minSize = 9999;
        int tempSize;
        Connection connection = null, temp;
        for (int i = 0; i < connections.size(); i++) {
            temp = connections.get(i);
            tempSize = temp.concurrentActionSize();
            if (tempSize < minSize) {
                connection = temp;
                minSize = tempSize;
            }
        }
        return connection;
    }


    @Override
    public List<Connection> getConnections() {
        return connections;
    }

    @Override
    public int size() {
        return connections.size();
    }


}
