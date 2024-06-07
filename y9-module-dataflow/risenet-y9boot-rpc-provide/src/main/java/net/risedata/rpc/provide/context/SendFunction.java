package net.risedata.rpc.provide.context;

import net.risedata.rpc.model.Response;

/**
 * @Description : 在调用send后执行的接口函数
 * @ClassName SendFunction
 * @Author lb
 * @Date 2021/11/22 11:09
 * @Version 1.0
 */
@FunctionalInterface
public interface SendFunction {


    void send( Response response,Send send) throws  Exception;
}
