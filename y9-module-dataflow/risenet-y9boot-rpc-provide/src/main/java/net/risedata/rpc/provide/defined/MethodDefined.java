package net.risedata.rpc.provide.defined;

import net.risedata.rpc.model.Request;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.filter.Filter;
import net.risedata.rpc.provide.filter.FilterContext;
import net.risedata.rpc.provide.filter.impl.InvokeMethodFilter;
import net.risedata.rpc.provide.filter.impl.SendResponseFilter;
import net.risedata.rpc.utils.ClassTools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 服务api的描述
 * @Author lb176
 * @Date 2021/4/29==16:30
 */
public class MethodDefined {
    private static final InvokeMethodFilter FINALLY_FILTER = new InvokeMethodFilter();
    private static final SendResponseFilter FIRST_FILTER  = new SendResponseFilter();

    /**
     * 具体执行的方法
     */
    private Method method;
    /**
     * 参数的类型
     */
    private List<ParameterDefined> parameterDefineds;
    /**
     * 类的定义
     */
    private ClassDefined classDefined;



    /**
     * 有效的参数长度
     */
    private int argsLength;
    /**
     * 是否为map key值对
     */
    private boolean isMap;

    private boolean isVoid;


    public ClassDefined getClassDefined() {
        return classDefined;
    }

    public void setClassDefined(ClassDefined classDefined) {
        this.classDefined = classDefined;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
        this.isVoid = method.getReturnType() == void.class;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public List<ParameterDefined> getParameterDefineds() {
        return parameterDefineds;
    }



    public void setParameterDefineds(List<ParameterDefined> parameterDefineds) {
        argsLength = parameterDefineds.size();
        for (ParameterDefined defined : parameterDefineds) {
            if (defined.getType() == RPCRequestContext.class) {
                argsLength--;
            }
        }
        this.parameterDefineds = parameterDefineds;
        if (argsLength > 1) {
            this.isMap = true;
        } else if (argsLength == 1) {
            Class<?> type = parameterDefineds.get(0).getType();
            this.isMap = !ClassTools.isBaseType(type);

        }
    }

    public boolean isMap() {
        return isMap;
    }

    public int getArgsLength() {
        return argsLength;
    }
}
