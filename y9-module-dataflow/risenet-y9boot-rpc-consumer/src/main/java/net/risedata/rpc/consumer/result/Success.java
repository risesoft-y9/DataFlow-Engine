package net.risedata.rpc.consumer.result;
/**
* @description: 调用成功执行的接口
* @Author lb176
* @Date 2021/7/30==10:01
*/
@FunctionalInterface
public interface Success {
    /**
     *  成功后调用的方法
     * @param res 返回值
     */
    void success(Result res);
}
