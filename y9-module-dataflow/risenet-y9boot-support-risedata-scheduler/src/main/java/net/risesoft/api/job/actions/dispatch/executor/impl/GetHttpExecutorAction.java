package net.risesoft.api.job.actions.dispatch.executor.impl;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.stereotype.Component;

/**
 * @Description :
 * @ClassName GetHttpExecutorAction
 * @Author lb
 * @Date 2022/9/21 15:28
 * @Version 1.0
 */
@Component("http-get")
public class GetHttpExecutorAction extends HttpExecutorAction {
    @Override
    HttpRequestBase getRequest(String url) {
        return new HttpGet(url);
    }
}
