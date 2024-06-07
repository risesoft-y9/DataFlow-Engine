package net.risedata.rpc.consumer.result.genericity;


import java.util.List;

import net.risedata.rpc.consumer.result.Error;

/**
 * @description: 返回值异步操作接口 list 类型
 * @Author lb176
 * @Date 2021/7/30==9:41
 */
public interface ListGenericitySyncResult<T> {

    /**
     * 当有返回值的时候调用
     *
     * @param success 调用成功后执行的方法
     * @return 当前对象
     */
    ListGenericitySyncResult onSuccess(ListGenericitySuccess<T> success);

    /**
     * 当调用失败的时候调用
     *
     * @param error 调用失败后执行的方法
     * @return 当前对象
     */
    ListGenericitySyncResult onError(Error error);


    /**
     * 拿到list 形式的返回值
     * @param <T> 类型
     * @return list<T> value
     */
    <T> List<T> getResult();
}
