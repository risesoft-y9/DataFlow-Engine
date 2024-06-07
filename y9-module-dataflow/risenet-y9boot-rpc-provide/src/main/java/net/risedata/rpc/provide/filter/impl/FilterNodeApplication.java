package net.risedata.rpc.provide.filter.impl;

import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;
import net.risedata.rpc.provide.filter.Filter;
import net.risedata.rpc.provide.filter.FilterNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description :
 * @ClassName FilterNodeApplication
 * @Author lb
 * @Date 2021/11/23 15:49
 * @Version 1.0
 */
public class FilterNodeApplication extends FilterNode {

    private List<FilterNode> filterNodes;

    public FilterNodeApplication(Filter fistFilter, Filter lastFilter) {
        filterNodes = new ArrayList<>();
        filterNodes.add(new DefaultFilterNode(filterNodes,fistFilter));
        filterNodes.add(new DefaultFilterNode(filterNodes,lastFilter));
        refreshIndex();
    }

    /**
     * 添加一个过滤器到中间
     *
     * @param filter
     * @param index
     */
    public void addFilter(Filter filter, int index) {
        if (index == 0) {
            index = 1;
        }else if(index>=this.filterNodes.size()){
            index = this.filterNodes.size()-1;
        }
        filterNodes.add(index,new DefaultFilterNode(filterNodes,filter));
        refreshIndex();
    }

    /**
     * 添加一个过滤器到
     *
     * @param filter
     */
    public void addFilter(Filter filter) {
        filterNodes.add(1,new DefaultFilterNode(filterNodes,filter));
        refreshIndex();
    }
    /**
     * 添加一个过滤器到
     *
     * @param filter
     */
    public void addFilterNode(FilterNode filter) {
        filterNodes.add(1,filter);
        refreshIndex();
    }

    private void refreshIndex(){
        for (int i = 0; i < filterNodes.size(); i++) {
            ((DefaultFilterNode)filterNodes.get(i)).setIndex(i);
        }
    }
    @Override
    public void doBefore(RPCRequestContext request) throws Exception {
        filterNodes.get(0).doBefore(request);
    }

    public List<FilterNode> getFilterNodes() {
        return filterNodes;
    }

    @Override
    public void doAfter(Response response, RPCRequestContext context, Send send) throws Exception {
        filterNodes.get(filterNodes.size()-1).doAfter(response,context,send);
    }
}
