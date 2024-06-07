package net.risedata.rpc.provide.filter;

import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;
import net.risedata.rpc.provide.filter.impl.FilterNodeApplication;
import net.risedata.rpc.provide.filter.impl.InvokeMethodFilter;
import net.risedata.rpc.provide.filter.impl.LinkedRequestFilter;
import net.risedata.rpc.provide.filter.impl.SendResponseFilter;

import java.util.ArrayList;
import java.util.List;


/**
 * 从filtercontext 进入后 调用applicationfiltercontext的dobefore 当application执行到最后一条则执行
 * 执行顺序 firstFilter --> applicationFilter --> apiFilter   --> lastFilter
 *
 * @Description : 过滤器上下文管理一个节点下的所有过滤器
 * @ClassName FilterContext
 * @Author lb
 * @Date 2021/11/22 11:31
 * @Version 1.0
 */
public abstract class FilterContext {

    /**
     * 全局filter 当 使用的 **  的时候则为全局过滤器
     */
    protected static FilterNodeApplication APPLICATION_FILTER;

    public static LinkedRequestFilter LINKEDR;

    static {
       refresh();
    }

    public static void refresh(){
        APPLICATION_FILTER = new FilterNodeApplication(new SendResponseFilter(), new InvokeMethodFilter());
        LINKEDR = new LinkedRequestFilter(APPLICATION_FILTER.getFilterNodes());
        APPLICATION_FILTER.addFilterNode(LINKEDR);
    }


    public static void doFilter(RPCRequestContext rpc) throws Exception {
        APPLICATION_FILTER.doBefore(rpc);
    }

    /**
     * 往下继续走
     *
     * @param request
     */
    public abstract void doBefore(RPCRequestContext request) throws Exception;

    /**
     * 当执行结束后回退
     *
     * @param response 返回值
     * @param context  上下文
     */
    public abstract void doAfter(Response response, RPCRequestContext context, Send send) throws Exception;


}
