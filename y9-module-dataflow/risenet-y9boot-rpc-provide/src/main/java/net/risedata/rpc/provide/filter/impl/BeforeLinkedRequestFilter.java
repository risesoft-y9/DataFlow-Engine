package net.risedata.rpc.provide.filter.impl;

import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;
import net.risedata.rpc.provide.filter.Filter;
import net.risedata.rpc.provide.filter.FilterContext;

/**
 * 此 过滤器应该放在request 过滤器中的最后一个位置当before 到此处时回退到linked 的位置
 * 此过滤器的after 不会被调用
 *
 * @Description : 用于连接request中的过滤器
 * @ClassName BeforeLinkedRequestFilter
 * @Author lb
 * @Date 2021/11/23 15:11
 * @Version 1.0
 */
public class BeforeLinkedRequestFilter extends  FilterAdapter {


    @Override
    public void doBefore(RPCRequestContext context, FilterContext filterContext) throws Exception {
        FilterContext.LINKEDR.doBefore(context);
    }


}
