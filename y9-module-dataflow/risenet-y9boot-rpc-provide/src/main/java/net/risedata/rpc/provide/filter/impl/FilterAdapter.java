package net.risedata.rpc.provide.filter.impl;

import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;
import net.risedata.rpc.provide.filter.Filter;
import net.risedata.rpc.provide.filter.FilterContext;

/**
 * @Description : 过滤器适配器提供空实现
 * @ClassName FilterAdapter
 * @Author lb
 * @Date 2021/11/23 17:48
 * @Version 1.0
 */
public class FilterAdapter implements Filter {
    @Override
    public void doBefore(RPCRequestContext context, FilterContext filterContext) throws Exception {

    }

    @Override
    public void doAfter(Response response, RPCRequestContext context, FilterContext filterContext, Send send) throws Exception {

    }

    @Override
    public int getOrder() {
        return 0;
    }


}
