package net.risedata.rpc.provide.handle.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.risedata.rpc.model.Request;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.defined.ParameterDefined;
import net.risedata.rpc.provide.handle.TypeConvertHandle;
import net.risedata.rpc.utils.LParameter;

import java.util.List;

public class DefaultTypeConvertHandle implements TypeConvertHandle {
    @Override
    public boolean isHandle(Class<?> type) {
        return true;
    }

    @Override
    public <T> T handleArray(JSONArray jsonArray, int index, Class<T> returnType, LParameter parameterDefined, RPCRequestContext request) {
        if(parameterDefined.isGeneral()){
            if (List.class.isAssignableFrom(returnType)){
                List res = jsonArray.getJSONArray(index).toJavaList(parameterDefined.getGeneralType()[0]);
                return returnType.cast(res);
            }

        }

        return jsonArray.getObject(index, returnType);
    }

    @Override
    public <T> T handleMap(JSONObject jsonObject, String name, Class<T> returnType, LParameter parameterDefined, RPCRequestContext request) {
        if(parameterDefined.isGeneral()){
            if (List.class.isAssignableFrom(returnType)){
                List res = jsonObject.getJSONArray(name).toJavaList(parameterDefined.getGeneralType()[0]);
                return returnType.cast(res);
            }

        }
        return jsonObject.getObject(name, returnType);
    }
}
