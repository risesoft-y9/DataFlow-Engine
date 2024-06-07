package net.risedata.rpc.consumer.result;

import net.risedata.rpc.consumer.result.genericity.GenericitySyncResult;
import net.risedata.rpc.consumer.result.genericity.ListGenericitySyncResult;

/**
 * @description: 返回值异步操作接口
 * @Author lb176
 * @Date 2021/7/30==9:41
 */
public interface SyncResult {

    /**
     * 当有返回值的时候调用
     *
     * @param success 调用成功后执行的方法
     * @return 当前对象
     */
    SyncResult onSuccess(Success success);

    /**
     * 当调用失败的时候调用
     *
     * @param error 调用失败后执行的方法
     * @return 当前对象
     */
    SyncResult onError(Error error);

    /**
     * 拿到list类型对应返回值
     *
     * @param returnType 返回值类型
     * @param <T>
     * @return list<T>
     */
    <T> ListGenericitySyncResult<T> asList(Class<T> returnType);

    /**
     * @param returnType 返回值类型
     * @param <T>
     * @return 返回值类型对应的异步接口
     */
    <T> GenericitySyncResult<T> as(Class<T> returnType);

    /**
     * 拿到 result
     * @return result
     */
    Result getResult();
}
