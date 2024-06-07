package net.risedata.rpc.consumer.core;

import io.netty.channel.ChannelFuture;
import net.risedata.rpc.consumer.listener.ConnectionListener;

/**
 * @description: 基础的连接管理器
 * @Author lb176
 * @Date 2021/7/30==10:31
 */
public interface BasedConnectionManager {
    /**
     * ps:每个connectionManger的实现有不同的格式
     * 根据一个特定的地址进行连接
     *
     * @param url  url
     */
    ChannelFuture connection(String url);

    /**
     * 根据 host和port进行连接
     *
     * @param host host
     * @param port port
     * @return 返回是否成功连接
     */
    ChannelFuture connectionToHost(String host, int port);


    /**
     * 根据 host和port进行连接
     *
     * @param host host
     * @param port port
     * @return 返回是否成功连接
     */
    boolean connectionAwait(String host, int port);


    /**
     * 添加一个连接的监听器
     *
     * @param listener 连接监听器
     */
    void addListener(ConnectionListener listener);

    /**
     * 关闭当前manager的所有连接
     */
    void close();

    /**
     * 拿到当前的连接池
     *
     * @return 连接池
     */
    ConnectionPool getConnectionPool();

    void stop();
}
