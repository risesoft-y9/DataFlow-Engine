package net.risedata.rpc.provide.filter;

import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;

/**
 * @Description : 过滤器节点
 * @ClassName FilterNode
 * @Author lb
 * @Date 2021/11/23 14:50
 * @Version 1.0
 */
public abstract class FilterNode  extends FilterContext {
    private Filter filter;

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }


}
