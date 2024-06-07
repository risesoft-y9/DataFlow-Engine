package net.risedata.rpc.provide.filter.impl;

import com.alibaba.fastjson.JSONArray;

import net.risedata.rpc.model.Request;
import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.config.ApplicationConfig;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;
import net.risedata.rpc.provide.defined.MethodDefined;
import net.risedata.rpc.provide.defined.ParameterDefined;
import net.risedata.rpc.provide.filter.Filter;
import net.risedata.rpc.provide.filter.FilterContext;
import net.risedata.rpc.provide.filter.FilterNode;
import net.risedata.rpc.provide.handle.ArgsMatchedHandle;
import net.risedata.rpc.provide.handle.impl.ArrayArgsMatchHandle;
import net.risedata.rpc.provide.handle.impl.MapArgsMatchHandle;
import net.risedata.rpc.provide.utils.ResponseBuilder;

import java.util.List;

/**
 * @Description : 执行具体方法的过滤器
 * @ClassName InvokeMethodFilter
 * @Author lb
 * @Date 2021/11/22 15:33
 * @Version 1.0
 */
public class InvokeMethodFilter  extends FilterAdapter {
    public static final ArgsMatchedHandle ARRAY = new ArrayArgsMatchHandle();
    public static final ArgsMatchedHandle MAP = new MapArgsMatchHandle();

    @Override
    public void doBefore(RPCRequestContext context, FilterContext filterContext) throws Exception {
        Request request = context.getRequest();
        MethodDefined handle = context.getMethodDefined();
        if (handle == null){

            filterContext.doAfter(ResponseBuilder.createSuccess(ResponseBuilder.createError(request.getUrl()+" 不存在映射",request), request), context, (res2) -> {

                context.send(res2);
            });
            return;
        }
        List<ParameterDefined> parameter = handle.getParameterDefineds();
        Object instance = handle.getClassDefined().getInstance();
        Object res = null;
        ParameterDefined p;

        if (parameter == null) {
            res = handle.getMethod().invoke(instance);
        } else {
            // 使用策略
            JSONArray args = request.getArgs();
            Object[] arg = new Object[parameter.size()];
            //使用map的方法
            if (ApplicationConfig.configArgs.runAbleMap) {
                if (MAP.isHandle(args, parameter, handle)) {
                    MAP.handle(arg, args, parameter, handle, context);
                } else {
                    ARRAY.handle(arg, args, parameter, handle, context);
                }
            } else {
                ARRAY.handle(arg, args, parameter, handle, context);
            }
            res = handle.getMethod().invoke(instance, arg);
        }
        if (handle.isVoid()) {
            context.setSendRunable((response, send) -> {

                filterContext.doAfter(ResponseBuilder.createSuccess(response, request), context,send );
            });
        } else {
            filterContext.doAfter(ResponseBuilder.createSuccess(res, request), context, (res2) -> {
                context.send(res2);
            });
        }
    }


}
