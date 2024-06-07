package net.risedata.rpc.provide.handle;

import com.alibaba.fastjson.JSONArray;

import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.defined.MethodDefined;
import net.risedata.rpc.provide.defined.ParameterDefined;

import java.util.List;

/**
* @description: 参数匹配的处理器
* @Author lb176
* @Date 2021/4/29==16:30
*/
public interface ArgsMatchedHandle {
    /**
     * 是否可以被此handle 匹配
     * @param args 传过来的参数
     * @param defineds 参数的定义信息合集
     * @return
     */
    boolean isHandle(JSONArray args,List<ParameterDefined> defineds,MethodDefined methodDefined);

    /**
     * 参数匹配
     * @param arg 需要放置的参数
     * @param args 参数集合
     * @param defined 参数的 定义信息
     */
    void handle(Object[] arg, JSONArray args, List<ParameterDefined> defined, MethodDefined methodDefined, RPCRequestContext request);
}
