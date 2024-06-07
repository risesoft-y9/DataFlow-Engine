package net.risedata.rpc.provide.filter.impl;


import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;
import net.risedata.rpc.provide.filter.FilterContext;

/**
 * @Description :第一根链条没有其他功能主要用来结尾
 * @ClassName FirstFilter
 * @Author lb
 * @Date 2021/11/22 15:31
 * @Version 1.0
 */
public class SendResponseFilter extends FilterAdapter {

    @Override
    public void doAfter(Response response, RPCRequestContext context, FilterContext filterContext, Send send) {
        send.send(response);
    }


}
