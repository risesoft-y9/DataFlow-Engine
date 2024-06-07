package net.risedata.rpc.consumer.invoke;

import net.risedata.rpc.consumer.core.Connection;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.exceptions.CarryRequestedException;
import net.risedata.rpc.consumer.listener.ExceptionListener;
import net.risedata.rpc.consumer.model.APIDescription;
import net.risedata.rpc.consumer.result.value.ReturnValueHandle;
import net.risedata.rpc.consumer.result.value.impl.SyncResultHandle;
import net.risedata.rpc.model.Request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * @description: 动态代理类时的实现
 * @Author lb176
 * @Date 2021/4/29==15:13
 */
public class ProxyInvoke implements InvocationHandler, InvokeHandle {
    /**
     * 同步连接管理器
     */
    private ConnectionManager connectionManager;
    /**
     * api 的名字
     */
    private String apiName;
    /**
     * 方法的配置
     */
    private Map<Method, APIDescription> apiMap;
    /**
     * 熔断
     */
    private Object fusing;
    /**
     * 异常处理
     */
    private ExceptionListener exceptionListener;


    public void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    public ProxyInvoke(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        APIDescription description = apiMap.get(method);
        return description.getInvokeHandle().invoke(connectionManager, description, method, args);

    }


    private Object toException(Method method, Object[] args, Exception e) throws Throwable {
        Object res = null;
        if (fusing != null) {
            res = method.invoke(fusing, args);
        }
        if (exceptionListener != null) {
            Request request = null;
            if (e instanceof CarryRequestedException) {
                request = ((CarryRequestedException) e).getRequest();
            }
            if (res != null) {
                exceptionListener.onException(e, method.getReturnType(), request);
            } else {
                res = exceptionListener.onException(e, method.getReturnType(), request);
            }
        }
        if (fusing == null && exceptionListener == null && res == null) {
            throw e;
        }
        return res;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void setApiMap(Map<Method, APIDescription> apiMap) {
        this.apiMap = apiMap;
    }

    public void setFusing(Object fusing) {
        this.fusing = fusing;
    }


    @Override
    public Object invoke(ConnectionManager connectionManager, APIDescription apiDescription, Method method, Object[] args) throws Throwable {

        try {
            ReturnValueHandle returnValueHandle = apiDescription.getReturnValueHandle();
            Connection connection = connectionManager.getConnection();
            return returnValueHandle.getValue(connection.executionSync(apiDescription.getMapping()
                            , apiDescription.getTimeOut(), args)
                            , apiDescription.getReturnType(), args);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(apiDescription);
            System.out.println(apiDescription.getReturnValueHandle());
            System.out.println(connectionManager);
            System.out.println(connectionManager.getConnection());
            System.out.println(apiDescription.getTimeOut());
            System.out.println(Arrays.toString(args));
            System.out.println(apiDescription.getReturnType());
            System.out.println(apiDescription.getMapping());
            return toException(method, args, e);
        }
    }
}
