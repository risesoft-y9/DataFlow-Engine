package net.risedata.rpc.consumer.result;

import net.risedata.rpc.model.Request;

/**
 * @description: 调用失败执行的接口
 * @Author lb176
 * @Date 2021/7/30==10:01
 */
@FunctionalInterface
public interface Error {
    /**
     * 失败后调用的方法
     *
     * @param res 返回值
     * @param e   异常
     */
    void error(Request res, Throwable e);
}
