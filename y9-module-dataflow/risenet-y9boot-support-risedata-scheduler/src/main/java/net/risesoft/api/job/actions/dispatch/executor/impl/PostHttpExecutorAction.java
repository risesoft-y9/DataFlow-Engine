package net.risesoft.api.job.actions.dispatch.executor.impl;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.stereotype.Component;

/**
 * @Description :
 * @ClassName GetHttpExecutorAction
 * @Author lb
 * @Date 2022/9/21 15:28
 * @Version 1.0
 */
@Component("http-post")
public class PostHttpExecutorAction extends HttpExecutorAction {
    @Override
    HttpRequestBase getRequest(String url) {
        return new HttpPost(url);
    }
}
