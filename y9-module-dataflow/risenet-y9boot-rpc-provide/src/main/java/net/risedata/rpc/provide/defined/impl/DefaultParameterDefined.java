package net.risedata.rpc.provide.defined.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.risedata.rpc.factory.ReturnTypeFactory;
import net.risedata.rpc.factory.model.ReturnType;
import net.risedata.rpc.provide.defined.ParameterDefined;
import net.risedata.rpc.utils.LParameter;


public class DefaultParameterDefined implements ParameterDefined {
    private boolean required;
    private String defaultValue;
    private Class<?> type;
    private String name;
    private LParameter parameter;

    public DefaultParameterDefined(boolean required, String defaultValue, Class<?> type, String name, LParameter parameter) {
        this.required = required;
        if (defaultValue != null) {
            this.defaultValue = JSON.toJSONString(defaultValue);
        }
        this.type = type;
        this.name = name;
        this.parameter = parameter;

    }

    @Override
    public String noPassMsg(int index, Object value, ParameterDefined parameterDefined) {
        return "param  name=" + parameterDefined.getName() + " is null " + "value:" + value;
    }

    @Override
    public LParameter getParameter() {
        return parameter;
    }


    @Override
    public boolean pass(Object value) {

        return !required || value != null;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public <T> T getDefaultValue(Class<T> returnType) {
        return JSON.parseObject(defaultValue, returnType);
    }


    @Override
    public String getName() {
        return name;
    }


}
