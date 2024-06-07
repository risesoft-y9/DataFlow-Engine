package net.risedata.rpc.consumer.core;

import io.netty.channel.ChannelInboundHandler;
import net.risedata.rpc.consumer.listener.ConnectionListener;
import net.risedata.rpc.consumer.model.PortAndHost;

/**
* @description: 连接中有host 和port 信息的connection
* @Author lb176
* @Date 2021/7/30==10:16
*/
public interface HostAndPortConnection extends ChannelInboundHandler{
    /**
     * 设置一个连接监听器
     *
     * @param listener 连接该连接状态的监听器
     */
    void setListener(ConnectionListener listener);

    /**
     * 关闭当前连接
     */
    void close();

    /**
     * 拿到当前连接的ip和端口
     *
     * @return ip和端口
     */
    PortAndHost getPortAndHost();

    /**
     * 该连接是否弃用
     * @return
     */
    boolean isUse();

    /**
     * 弃用该连接调用此方法后会平滑关闭该连接
     * 先调整为弃用后调用关闭事件等到全部执行完成后断开连接
     */
    void abandon();

    /**
     * 启用 如果这个连接已经被关闭了则返回false
     */
    boolean enable();
}
