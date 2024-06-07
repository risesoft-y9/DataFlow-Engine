package net.risedata.rpc.provide.listener;



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
    SyncResult onError(ListenerError error);

    /**
     * 拿到 result
     * @return result
     */
    Result getResult();
}
