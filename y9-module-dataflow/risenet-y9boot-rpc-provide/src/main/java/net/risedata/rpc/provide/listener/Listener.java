package net.risedata.rpc.provide.listener;

import net.risedata.rpc.provide.net.ClinetConnection;

/**
* @description: 过滤器
* @Author lb176
* @Date 2021/4/29==10:02
*/
public interface Listener {

    /**
     * 当有客户端连接
     * @param clinetConnection
     */
    void onConnection(ClinetConnection clinetConnection);

    /**
     * 当客户端连接被关闭
     * @param clinetConnection
     */
    void onClosed(ClinetConnection clinetConnection);
}
