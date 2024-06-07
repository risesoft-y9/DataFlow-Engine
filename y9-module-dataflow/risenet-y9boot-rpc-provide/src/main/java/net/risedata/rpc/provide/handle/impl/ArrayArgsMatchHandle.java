package net.risedata.rpc.provide.handle.impl;

import com.alibaba.fastjson.JSONArray;

import net.risedata.rpc.model.Request;
import net.risedata.rpc.provide.config.ApplicationConfig;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.defined.MethodDefined;
import net.risedata.rpc.provide.defined.ParameterDefined;
import net.risedata.rpc.provide.exceptions.ArgsException;
import net.risedata.rpc.provide.handle.ArgsMatchedHandle;
import net.risedata.rpc.provide.handle.TypeConvertHandle;
import net.risedata.rpc.utils.LParameter;

import java.util.List;

/**
 * array形式的参数填充器
 */
public class ArrayArgsMatchHandle implements ArgsMatchedHandle {


    @Override
    public boolean isHandle(JSONArray args, List<ParameterDefined> defineds, MethodDefined methodDefined) {
        return true;
    }

    @Override
    public void handle(Object[] arg, JSONArray args, List<ParameterDefined> defined, MethodDefined methodDefined, RPCRequestContext request) {
        ParameterDefined parameterDefined;
        boolean isTypeConvert = methodDefined.getClassDefined().isTypeConvert();
        for (int i = 0; i < defined.size(); i++) {
            parameterDefined = defined.get(i);
            if (isTypeConvert) {
                arg[i] = getValue(args, i, parameterDefined.getType(), methodDefined.getClassDefined().getTypeConvertHandles(), parameterDefined.getParameter(), request);
            } else {
                arg[i] = ApplicationConfig.DEFAULT_TYPE_CONVERT_HANDLE.handleArray(args, i, parameterDefined.getType(), parameterDefined.getParameter(), request);
            }
            if (!parameterDefined.pass(arg[i])) {
                throw new ArgsException(parameterDefined.noPassMsg(i, arg[i], parameterDefined));
            }
            if (arg[i] == null) {
                arg[i] = parameterDefined.getDefaultValue(parameterDefined.getType());
            }
        }

    }

    public Object getValue(JSONArray args, int index, Class<?> returnType, List<TypeConvertHandle> typeConvertHandles, LParameter parameterDefined, RPCRequestContext request) {
        for (TypeConvertHandle typeConvertHandle : typeConvertHandles) {
            if (typeConvertHandle.isHandle(returnType)) {
                return typeConvertHandle.handleArray(args, index, returnType, parameterDefined, request);
            }
        }
        if (args == null) {
            return null;
        }
        return ApplicationConfig.DEFAULT_TYPE_CONVERT_HANDLE.handleArray(args, index, returnType, parameterDefined, request);
    }
}
