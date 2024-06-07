package net.risedata.rpc.provide.filter.impl;

import net.risedata.rpc.model.Request;
import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;
import net.risedata.rpc.provide.filter.Filter;
import net.risedata.rpc.provide.filter.FilterContext;
import net.risedata.rpc.provide.filter.FilterNode;

import java.util.List;

/**
 * @Description : 用来连接 requestFilter里面的过滤器
 * @ClassName LinkedRequestFilter
 * @Author lb
 * @Date 2021/11/23 17:04
 * @Version 1.0
 */
public class LinkedRequestFilter extends DefaultFilterNode implements Filter {



    public LinkedRequestFilter(List<FilterNode> filters) {
        super(filters, null);
        setFilter(this);
    }

    @Override
    public void doBefore(RPCRequestContext context, FilterContext filterContext) throws Exception {
        if (context.getFilterApplication() != null) {
            context.getFilterApplication().doBefore(context);
        } else {
            filterContext.doBefore(context);
        }
    }





    @Override
    public void doAfter(Response response, RPCRequestContext context, FilterContext filterContext, Send send) throws Exception{
        if (context.getFilterApplication() != null) {
            context.getFilterApplication().doAfter(response,context,send);
        } else {
            filterContext.doAfter(response,context,send);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }


}
