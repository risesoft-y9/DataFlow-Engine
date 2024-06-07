package net.risedata.register.api.filter.listener;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @Description :
 * @ClassName HttpListener
 * @Author lb
 * @Date 2022/7/4 16:42
 * @Version 1.0
 */
public interface HttpListener {

    void onStart(ServletRequest request, ServletResponse response);

    void onEnd(ServletRequest request, ServletResponse response);

    void onError(ServletRequest request, ServletResponse response,Exception e);
}
