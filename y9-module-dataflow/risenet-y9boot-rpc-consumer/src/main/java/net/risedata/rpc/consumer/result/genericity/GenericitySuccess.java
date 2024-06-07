package net.risedata.rpc.consumer.result.genericity;
/**
* @description: 调用成功执行的接口 泛型形式的
* @Author lb176
* @Date 2021/7/30==10:01
*/
@FunctionalInterface
public interface GenericitySuccess<T> {
    /**
     *  成功后调用的方法
     * @param res 返回值
     */
    void success(T res);
}
