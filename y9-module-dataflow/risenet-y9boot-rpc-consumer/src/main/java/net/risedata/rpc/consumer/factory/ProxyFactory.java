package net.risedata.rpc.consumer.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @description: 动态代理工厂
 * @Author lb176
 * @Date 2021/4/29==15:13
 */
class ProxyFactory {

    static Object getProxy(Class<?>[] interfaces, InvocationHandler handler) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, handler);
    }
}
