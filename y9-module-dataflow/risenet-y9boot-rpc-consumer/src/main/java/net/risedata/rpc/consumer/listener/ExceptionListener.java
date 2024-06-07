package net.risedata.rpc.consumer.listener;

import net.risedata.rpc.model.Request;

/**
 * @description: 出现异常调用的统一接口 ps:用于动态代理出来的接口调用
 * @Author lb176
 * @Date 2021/4/29==15:14
 */
public interface ExceptionListener {
    /**
     * 出现异常执行
     *
     * @param e          异常信息
     * @param returnType 返回类型
     * @param request    当前执行的参数
     * @param <T>        返回值
     * @return 返回值:当没有降级的时候使用此返回值当存在降级的时候使用降级的返回值
     */
    <T> T onException(Throwable e, Class<T> returnType, Request request);
}
