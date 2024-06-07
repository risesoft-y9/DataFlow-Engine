package net.risedata.rpc.provide.handle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.risedata.rpc.model.Request;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.defined.ParameterDefined;
import net.risedata.rpc.utils.LParameter;

/**
 * @description: 类型转换的处理器
 * @Author lb176
 * @Date 2021/4/29==16:31
 */
public interface TypeConvertHandle {
    /**
     * 处理的类型
     *
     * @param type 类型
     * @return 返回true 则使用此处理器处理
     */
    boolean isHandle(Class<?> type);

    /**
     * 处理类型array类型对象
     *
     * @param jsonArray  jsonArray
     * @param index      根据不同的 argsMatch 来处理不同的value
     * @param returnType 返回类型
     * @param <T>        返回值
     * @param parameter  参数定义
     * @return 返回对应返回类型的返回值
     */
    <T> T handleArray(JSONArray jsonArray, int index, Class<T> returnType, LParameter parameter, RPCRequestContext  request);

    /**
     * 处理map
     *
     * @param jsonObject json 的objectMap
     * @param name       参数名
     * @param returnType 返回值
     * @param <T>        返回值
     * @param parameter      参数定义
     * @return 返回值对应的类型
     */
    <T> T handleMap(JSONObject jsonObject, String name, Class<T> returnType, LParameter parameter, RPCRequestContext request);

}
