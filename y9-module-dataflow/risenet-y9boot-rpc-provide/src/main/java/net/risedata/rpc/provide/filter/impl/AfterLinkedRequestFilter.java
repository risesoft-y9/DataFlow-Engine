package net.risedata.rpc.provide.filter.impl;

import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;
import net.risedata.rpc.provide.filter.Filter;
import net.risedata.rpc.provide.filter.FilterContext;

/**
 * 此 过滤器应该放在request 过滤器中的第一个位置当after 到此处时回退到linked 的位置

 *
 * @Description : 用于连接request中的过滤器
 * @ClassName AfterLinkedRequestFilter
 * @Author lb
 * @Date 2021/11/23 15:11
 * @Version 1.0
 */
public class AfterLinkedRequestFilter extends  FilterAdapter {

    @Override
    public void doAfter(Response response, RPCRequestContext context, FilterContext filterContext, Send send) throws Exception {
        FilterContext.LINKEDR.doAfter(response,context,send);
    }
}
