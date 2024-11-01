package net.risesoft.security;

import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.annotation.RPCFilter;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;
import net.risedata.rpc.provide.filter.Filter;
import net.risedata.rpc.provide.filter.FilterContext;

import org.springframework.stereotype.Component;

/**
 * @Description :
 * @ClassName RPCRequestFilter
 * @Author lb
 * @Date 2022/8/22 16:50
 * @Version 1.0
 */
@RPCFilter
@Component
public class RPCRequestFilter implements Filter {
	
    private static ThreadLocal<RPCRequestContext> THREAD_LOCAL = new ThreadLocal<RPCRequestContext>();
    
    @Override
    public void doBefore(RPCRequestContext context, FilterContext filterContext) throws Exception {
        THREAD_LOCAL.set(context);
        filterContext.doBefore(context);
    }

    @Override
    public void doAfter(Response response, RPCRequestContext context, FilterContext filterContext, Send send) throws Exception {
        THREAD_LOCAL.remove();
        filterContext.doAfter(response, context, send);
    }

    public static RPCRequestContext getCurrent(){
        return THREAD_LOCAL.get();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
