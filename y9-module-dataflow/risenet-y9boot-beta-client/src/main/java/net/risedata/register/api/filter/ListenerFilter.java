package net.risedata.register.api.filter;

import org.springframework.beans.factory.annotation.Autowired;

import net.risedata.register.api.filter.listener.HttpListener;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description :
 * @ClassName ListenerFilter
 * @Author lb
 * @Date 2022/7/4 16:36
 * @Version 1.0
 */
public class ListenerFilter implements Filter {
    @Autowired
    private List<HttpListener> httpListeners;



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {


        for (HttpListener httpListener : httpListeners) {
            httpListener.onStart(request, response);
        }
        try {
            chain.doFilter(request, response);
        } catch (IOException e) {
            for (HttpListener httpListener : httpListeners) {
                httpListener.onError(request, response,e);
            }
            throw e;
        } catch (ServletException e) {
            for (HttpListener httpListener : httpListeners) {
                httpListener.onError(request, response,e);
            }
            throw e;
        }finally {
            for (HttpListener httpListener : httpListeners) {
                httpListener.onEnd(request, response);
            }
        }



    }



}
