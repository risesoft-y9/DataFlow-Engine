package net.risedata.rpc.consumer.invoke;

import java.lang.reflect.Method;

import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.model.APIDescription;

/**
 * @description: 执行接口方法发送的接口
 * @Author lb176
 * @Date 2021/8/5==9:40
 */
public interface InvokeHandle {
    /**
     * 在执行方法的时候会调用此方法
     * @param connectionManager 对应的连接管理器
     * @param apiDescription 需要发送的api的描述
     * @param method 方法
     * @param args 参数
     * @return 返回此方法的返回值
     */
    Object invoke(ConnectionManager connectionManager, APIDescription apiDescription, Method method, Object[] args)throws Throwable;

}
