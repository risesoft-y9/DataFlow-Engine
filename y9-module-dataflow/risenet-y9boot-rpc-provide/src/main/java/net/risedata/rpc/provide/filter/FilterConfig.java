package net.risedata.rpc.provide.filter;

/**
 * @Description : 过滤器的配置
 * @ClassName FilterConfig
 * @Author lb
 * @Date 2021/11/24 9:49
 * @Version 1.0
 */
public class FilterConfig implements Comparable<FilterConfig>{
    /**
     * 那些符合匹配**为全部
     */
    private String matcher;
    /**
     * 不匹配的url **为没有不匹配的
     */
    private String noMatcher;

    private Filter filter;

    public FilterConfig(String matcher, String noMatcher, Filter filter) {
        this.matcher = matcher;
        this.noMatcher = noMatcher;
        this.filter = filter;
    }
    public FilterConfig( Filter filter) {
       this("**","**",filter);

    }

    public String getMatcher() {
        return matcher;
    }

    public void setMatcher(String matcher) {
        this.matcher = matcher;
    }

    public String getNoMatcher() {
        return noMatcher;
    }

    public void setNoMatcher(String noMatcher) {
        this.noMatcher = noMatcher;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public int compareTo(FilterConfig o) {
        return o.filter.getOrder()-filter.getOrder();
    }

    @Override
    public String toString() {
        return "FilterConfig{" +
                "matcher='" + matcher + '\'' +
                ", noMatcher='" + noMatcher + '\'' +
                ", filter=" + filter +
                '}';
    }
}
