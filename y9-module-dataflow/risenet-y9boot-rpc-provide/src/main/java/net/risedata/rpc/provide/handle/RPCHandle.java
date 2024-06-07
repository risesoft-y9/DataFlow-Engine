package net.risedata.rpc.provide.handle;


import net.risedata.rpc.model.Request;

/**
 * @description: rpc的handle 统一父接口
 * @Author lb176
 * @Date 2021/4/29==16:31
 */
public interface RPCHandle {

    void handle(Request request);
}
