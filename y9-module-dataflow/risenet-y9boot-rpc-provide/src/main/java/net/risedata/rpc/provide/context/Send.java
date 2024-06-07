package net.risedata.rpc.provide.context;

import net.risedata.rpc.model.Response;

/**
 * @Description : 发送接口
 * @ClassName Send
 * @Author lb
 * @Date 2021/11/22 11:23
 * @Version 1.0
 */
public interface Send {
    void send(Response response);
}
