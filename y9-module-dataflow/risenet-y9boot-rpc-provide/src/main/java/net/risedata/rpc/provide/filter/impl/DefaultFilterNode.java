package net.risedata.rpc.provide.filter.impl;

import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;
import net.risedata.rpc.provide.filter.Filter;
import net.risedata.rpc.provide.filter.FilterContext;
import net.risedata.rpc.provide.filter.FilterNode;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;

import java.util.List;

/**
 * @Description : 默认的过滤器
 * @ClassName DefaultFilter
 * @Author lb
 * @Date 2021/11/23 15:11
 * @Version 1.0
 */
public class DefaultFilterNode extends FilterNode {

    private List<FilterNode> filters;

    private int index;

    public DefaultFilterNode(List<FilterNode> filters, Filter filter) {
        this.filters = filters;
        setFilter(filter);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void doBefore(RPCRequestContext request) throws Exception {
        FilterNode filterContext =filters.get((index+1));
        filterContext.getFilter().doBefore(request,filterContext);
    }

    @Override
    public void doAfter(Response response, RPCRequestContext context, Send send) throws Exception {
        FilterNode filterContext =filters.get(index-1);
        filterContext.getFilter().doAfter(response,context,filterContext,send);
    }

}
