package net.risedata.rpc.consumer.invoke.impl;

import java.lang.reflect.Method;
import java.util.List;

import net.risedata.rpc.consumer.core.Connection;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.core.ConnectionPool;
import net.risedata.rpc.consumer.invoke.InvokeHandle;
import net.risedata.rpc.consumer.model.APIDescription;

/**
 * @description: 对连接管理器中所有的连接进行循环发送直到返回值是true 才返回
 * ps: 使用此执行器需要返回值为boolean
 * @Author lb176
 * @Date 2021/8/5==9:59
 */
public class ForBooleanTrueInvokeHandle implements InvokeHandle {
    @Override
    public Object invoke(ConnectionManager connectionManager, APIDescription apiDescription, Method method, Object[] args) throws Throwable {
        List<Connection> connections = connectionManager.getConnectionPool().getConnections();
        for (int i = 0; i < connections.size(); i++) {

            if (connections.get(i)
                    .executionSync(apiDescription.getMapping(), apiDescription.getTimeOut(), args)
                    .getResult().getValue(Boolean.class)) {
                return true;
            };
        }
        return false;
    }
}
