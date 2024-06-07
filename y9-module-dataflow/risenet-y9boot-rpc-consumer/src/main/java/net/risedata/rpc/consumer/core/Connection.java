package net.risedata.rpc.consumer.core;

import java.util.List;

import net.risedata.rpc.consumer.result.SyncResult;
import net.risedata.rpc.consumer.result.genericity.GenericitySyncResult;
import net.risedata.rpc.consumer.result.genericity.ListGenericitySyncResult;

/**
 * @description: 与服务器连接的接口
 * @Author lb176
 * @Date 2021/4/29==14:45
 */
public interface Connection extends HostAndPortConnection {
    /**
     * 执行远程调用
     *
     * @param service    调用的远程服务名
     * @param api        api 服务名字
     * @param returnType 返回值
     * @param timeOut    超时时间 如果是0 则不会有超时发生会一直等待
     * @param args       参数
     * @param <T>        返回值
     * @return 返回 服务端返回的值
     */
    @Deprecated
    <T> T execution(String service, String api, Class<T> returnType, int timeOut, Object... args);

    /**
     * 执行远程调用 返回list类型
     *
     * @param service    调用的远程服务名
     * @param api        api 服务名字
     * @param returnType 返回值
     * @param timeOut    超时时间 如果是0 则不会有超时发生会一直等待
     * @param args       参数
     * @param <T>        返回值
     * @return 返回 服务端返回的值
     */
    @Deprecated
    <T> List<T> executionList(String service, String api, Class<T> returnType, int timeOut, Object... args);


    /**
     * 执行远程调用
     *
     * @param service 调用的远程服务名
     * @param api     api 服务名字
     * @param timeOut 超时时间 如果是0 则不会有超时发生会一直等待
     * @param args    参数
     * @return 返回 服务端返回的值
     */
    @Deprecated
    SyncResult executionSync(String service, String api, int timeOut, Object... args);

    /**
     * 远程调用一个服务并装换为对应的类型
     *
     * @param service serviceName
     * @param api     apiName
     * @param timeOut 超时时间 小于=0则不生效
     * @param type    返回值
     * @param args    参数
     * @param <T>
     * @return 对应的类型返回值
     */
    @Deprecated
    <T> GenericitySyncResult<T> executionSyncAs(String service, String api, int timeOut, Class<T> type, Object... args);

    /**
     * 远程调用一个服务并装换为对应的lsit类型
     *
     * @param service serviceName
     * @param api     apiName
     * @param timeOut 超时时间 小于=0则不生效
     * @param type    返回值
     * @param args    参数
     * @param <T>
     * @return 对应的类型返回值
     */
    @Deprecated
    <T> ListGenericitySyncResult<T> executionSyncAsList(String service, String api, int timeOut, Class<T> type, Object... args);

    /**
     * 这个连接是否被移除
     *
     * @return
     */
    boolean isRemoved();

    /**
     * 拿到当前连接 是否曾经连接过
     *
     * @return
     */
    boolean isHasBeenLinked();

    /**
     * 执行远程调用
     * service+"/"+api = mapping
     *
     * @param mapping
     * @param returnType 返回值
     * @param timeOut    超时时间 如果是0 则不会有超时发生会一直等待
     * @param args       参数
     * @param <T>        返回值
     * @return 返回 服务端返回的值
     */
    <T> T execution(String mapping, Class<T> returnType, int timeOut, Object... args);

    /**
     * 执行远程调用 返回list类型
     * service+"/"+api = mapping
     *
     * @param mapping
     * @param returnType 返回值
     * @param timeOut    超时时间 如果是0 则不会有超时发生会一直等待
     * @param args       参数
     * @param <T>        返回值
     * @return 返回 服务端返回的值
     */
    <T> List<T> executionList(String mapping, Class<T> returnType, int timeOut, Object... args);


    /**
     * 执行远程调用
     * service+"/"+api = mapping
     *
     * @param mapping mapping
     * @param timeOut 超时时间 如果是0 则不会有超时发生会一直等待
     * @param args    参数
     * @return 返回 服务端返回的值
     */
    SyncResult executionSync(String mapping, int timeOut, Object... args);

    /**
     * 远程调用一个服务并装换为对应的类型
     * service+"/"+api = mapping
     *
     * @param mapping mapping
     * @param timeOut 超时时间 小于=0则不生效
     * @param type    返回值
     * @param args    参数
     * @param <T>
     * @return 对应的类型返回值
     */
    <T> GenericitySyncResult<T> executionSyncAs(String mapping, int timeOut, Class<T> type, Object... args);

    /**
     * 远程调用一个服务并装换为对应的lsit类型
     * service+"/"+api = mapping
     *
     * @param mapping mapping 服务端对于映射
     * @param timeOut 超时时间 小于=0则不生效
     * @param type    返回值
     * @param args    参数
     * @param <T>
     * @return 对应的类型返回值
     */
    <T> ListGenericitySyncResult<T> executionSyncAsList(String mapping, int timeOut, Class<T> type, Object... args);

    /**
     * 拿到当前连接的连接管理器
     *
     * @return
     */
    ConnectionManager getConnectionManager();

    /**
     * 当前执行的任务堆积数量
     *
     * @return
     */
    int concurrentActionSize();

}
