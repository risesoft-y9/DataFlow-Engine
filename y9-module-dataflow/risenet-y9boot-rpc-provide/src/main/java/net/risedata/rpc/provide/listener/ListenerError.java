package net.risedata.rpc.provide.listener;

import net.risedata.rpc.provide.model.ListenerRequest;

/**
 * @description: 调用失败执行的接口
 * @Author lb176
 * @Date 2021/7/30==10:01
 */
@FunctionalInterface
public interface ListenerError {
    /**
     * 失败后调用的方法
     *
     * @param res 返回值
     * @param e   异常
     */
    void error(ListenerRequest res, Throwable e);
}
