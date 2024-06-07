package net.risedata.rpc.provide.handle.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.risedata.rpc.model.Request;
import net.risedata.rpc.provide.config.ApplicationConfig;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.defined.MethodDefined;
import net.risedata.rpc.provide.defined.ParameterDefined;
import net.risedata.rpc.provide.exceptions.ArgsException;
import net.risedata.rpc.provide.filter.impl.InvokeMethodFilter;
import net.risedata.rpc.provide.handle.ArgsMatchedHandle;
import net.risedata.rpc.provide.handle.TypeConvertHandle;
import net.risedata.rpc.utils.LParameter;

import java.util.List;

/**
 * map形式的参数填充器
 */
public class MapArgsMatchHandle implements ArgsMatchedHandle {


    @Override
    public boolean isHandle(JSONArray args, List<ParameterDefined> defineds, MethodDefined methodDefined) {
        return args.size() == 1;
    }

    @Override
    public void handle(Object[] arg, JSONArray args, List<ParameterDefined> defined, MethodDefined methodDefined, RPCRequestContext request) {
        ParameterDefined parameterDefined;
        boolean isTypeConvert = methodDefined.getClassDefined().isTypeConvert();

        if (methodDefined.isMap() && defined.size() == 1) {
            InvokeMethodFilter.ARRAY.handle(arg, args, defined, methodDefined, request);
        } else {
            JSONObject object = args.getJSONObject(0);

            for (int i = 0; i < defined.size(); i++) {
                parameterDefined = defined.get(i);
                if (isTypeConvert) {
                    arg[i] = getValue(object, parameterDefined.getName(), parameterDefined.getType(), methodDefined.getClassDefined().getTypeConvertHandles(),parameterDefined.getParameter(), request);
                } else {
                    arg[i] = ApplicationConfig.DEFAULT_TYPE_CONVERT_HANDLE.handleMap(object, parameterDefined.getName(), parameterDefined.getType(),parameterDefined.getParameter(), request);
                }
                if (!parameterDefined.pass(arg[i])) {
                    throw new ArgsException(parameterDefined.noPassMsg(i, arg[i], parameterDefined));
                }
                if (arg[i] == null) {
                    arg[i] = parameterDefined.getDefaultValue(parameterDefined.getType());
                }
            }
        }

    }

    private Object getValue(JSONObject jsonObject, String name, Class<?> returnType, List<TypeConvertHandle> typeConvertHandles, LParameter parameterDefined, RPCRequestContext request) {
        for (TypeConvertHandle typeConvertHandle : typeConvertHandles) {
            if (typeConvertHandle.isHandle(returnType)) {
                return typeConvertHandle.handleMap(jsonObject, name, returnType,parameterDefined, request);
            }
        }
        return ApplicationConfig.DEFAULT_TYPE_CONVERT_HANDLE.handleMap(jsonObject, name, returnType,parameterDefined, request);
    }
}
