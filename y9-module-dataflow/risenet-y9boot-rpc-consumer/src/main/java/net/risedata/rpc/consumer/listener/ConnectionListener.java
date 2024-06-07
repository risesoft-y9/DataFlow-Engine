package net.risedata.rpc.consumer.listener;



import net.risedata.rpc.consumer.core.HostAndPortConnection;

/**
* @description: 连接监听器
* @Author lb176
* @Date 2021/4/29==15:14
*/
public interface ConnectionListener {
    /**
     * 连接时触发
     * @param connection 连接
     */
    void onConnection(HostAndPortConnection connection);

    /**
     * 关闭时触发
     * @param connection 被关闭的connection
     */
    void onClose(HostAndPortConnection connection);
}
