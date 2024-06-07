package net.risedata.rpc.provide.filter;


import ch.qos.logback.core.joran.spi.ElementSelector;
import net.risedata.rpc.model.Request;
import net.risedata.rpc.provide.annotation.RPCFilter;
import net.risedata.rpc.provide.config.Application;
import net.risedata.rpc.provide.config.ApplicationConfig;
import net.risedata.rpc.provide.filter.impl.AfterLinkedRequestFilter;
import net.risedata.rpc.provide.filter.impl.BeforeLinkedRequestFilter;
import net.risedata.rpc.provide.filter.impl.FilterNodeApplication;
import net.risedata.rpc.utils.AntPathMatcher;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description :管理过滤器的操作
 * @ClassName FilterManager
 * @Author lb
 * @Date 2021/11/22 10:32
 * @Version 1.0
 */
public class FilterManager implements ApplicationListener<ContextRefreshedEvent> {


    public static final String ALL = "**";

    private static final AfterLinkedRequestFilter AFTER_LINKED_REQUEST_FILTER = new AfterLinkedRequestFilter();

    private static final BeforeLinkedRequestFilter BEFORE_LINKED_REQUEST_FILTER = new BeforeLinkedRequestFilter();

    private static List<FilterConfig> filterConfigs = new ArrayList<>();
    private static final AntPathMatcher MATCHER = new AntPathMatcher();


    private static final Map<String, FilterContextCache> FILTER_CONTEXT_CACHE_MAP = new ConcurrentHashMap<>();



    public static FilterContextCache createFilterContext(String url, Request request) {
        FilterContextCache cache = FILTER_CONTEXT_CACHE_MAP.get(url);
        if (cache == null) {
            synchronized (url.intern()) {
                if (filterConfigs.size() > 0) {
                    cache = FILTER_CONTEXT_CACHE_MAP.get(url);
                    if (cache == null) {
                        cache = new FilterContextCache();
                        List<Filter> filters = new ArrayList<>();
                        for (FilterConfig filterConfig : filterConfigs) {
                            if (MATCHER.match(filterConfig.getMatcher(), url)) {
                                if ("**".equals(filterConfig.getNoMatcher())) {
                                    filters.add(filterConfig.getFilter());
                                } else {
                                    if (!MATCHER.match(filterConfig.getNoMatcher(), url)) {
                                        filters.add(filterConfig.getFilter());
                                    }
                                }
                            }
                        }

                        if (filters.size() > 0) {
                            //添加一个linked过滤器
                            FilterNodeApplication filterNodeApplication = new FilterNodeApplication(AFTER_LINKED_REQUEST_FILTER, BEFORE_LINKED_REQUEST_FILTER);
                            for (Filter filter : filters) {
                                filterNodeApplication.addFilter(filter);
                            }
                            cache.setFilters(filterNodeApplication);
                        }
                        cache.setMethodDefined(ApplicationConfig.getMethodDefined(request.getUrl()));
                        FILTER_CONTEXT_CACHE_MAP.put(url, cache);
                    }
                } else {
                    cache = new FilterContextCache();
                    cache.setMethodDefined(ApplicationConfig.getMethodDefined(request.getUrl()));

                    FILTER_CONTEXT_CACHE_MAP.put(url, cache);
                }
            }

        }
        return cache;

    }

    public static void clear(){
        filterConfigs.clear();
        FILTER_CONTEXT_CACHE_MAP.clear();
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (filterConfigs.size() == 0) {
            Application.logger.info("load filter ");
            Collection<Filter> filters = event.getApplicationContext().getBeansOfType(Filter.class).values();
            List<Filter> applicationFilters = new ArrayList<>();
            RPCFilter rpcFilter;
            for (Filter filter : filters) {
                rpcFilter = AnnotationUtils.findAnnotation(filter.getClass(), RPCFilter.class);

                if (rpcFilter == null) {
                    filterConfigs.add(new FilterConfig(filter));
                } else {
                    if (ALL.equals(rpcFilter.value()) && ALL.equals(rpcFilter.noMatcher())) {
                        applicationFilters.add(filter);
                    } else {
                        filterConfigs.add(new FilterConfig(rpcFilter.value(), rpcFilter.noMatcher(), filter));
                    }
                }
            }
            if (applicationFilters.size() > 0) {
                applicationFilters.sort((v, v2) -> v2.getOrder() - v.getOrder());
                for (Filter applicationFilter : applicationFilters) {
                    FilterContext.APPLICATION_FILTER.addFilter(applicationFilter);
                }
            }
            filterConfigs.sort(FilterConfig::compareTo);
        }

    }



}
