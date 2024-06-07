package net.risedata.rpc.consumer.result.genericity;

import net.risedata.rpc.consumer.result.Error;
import net.risedata.rpc.consumer.result.Result;

/**
 * @description: 返回值异步操作接口
 * @Author lb176
 * @Date 2021/7/30==9:41
 */
public interface GenericitySyncResult<T> {

    /**
     * 当有返回值的时候调用
     *
     * @param success 调用成功后执行的方法
     * @return 当前对象
     */
    GenericitySyncResult<T> onSuccess(GenericitySuccess<T> success);

    /**
     * 当调用失败的时候调用
     *
     * @param error 调用失败后执行的方法
     * @return 当前对象
     */
    GenericitySyncResult<T> onError(Error error);


    /**
     * 以同步的方式拿到返回值
     * @return 返回值
     */
    <T> T getResult();
}
