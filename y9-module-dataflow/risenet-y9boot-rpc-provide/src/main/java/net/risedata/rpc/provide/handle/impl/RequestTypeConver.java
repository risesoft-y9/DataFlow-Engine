package net.risedata.rpc.provide.handle.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.defined.ParameterDefined;
import net.risedata.rpc.provide.handle.TypeConvertHandle;
import net.risedata.rpc.utils.LParameter;

/**
* @description: request参数处理
* @Author lb176
* @Date 2021/8/25==15:05
*/
public class RequestTypeConver implements TypeConvertHandle {
    @Override
    public boolean isHandle(Class<?> type) {
        return type==RPCRequestContext.class;
    }

    @Override
    public <T> T handleArray(JSONArray jsonArray, int index, Class<T> returnType, LParameter parameterDefined, RPCRequestContext request) {
        return returnType.cast(request);
    }

    @Override
    public <T> T handleMap(JSONObject jsonObject, String name, Class<T> returnType, LParameter parameterDefined, RPCRequestContext request) {
        return returnType.cast(request);
    }
}
