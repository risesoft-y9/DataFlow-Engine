package net.risedata.rpc.provide.listener;

import io.netty.channel.socket.SocketChannel;

/**
 * @description: 监听管理器
 * @Author lb176
 * @Date 2021/4/29==9:58
 */
public interface ListenerManager  extends  Listener{


    /**
     * 添加一个监听器
     * @param listener 监听器
     */
    void addListener(Listener listener);

}
