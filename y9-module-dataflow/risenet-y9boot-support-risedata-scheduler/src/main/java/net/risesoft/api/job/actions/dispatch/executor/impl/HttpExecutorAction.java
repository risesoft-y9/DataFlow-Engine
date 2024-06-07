package net.risesoft.api.job.actions.dispatch.executor.impl;


import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.dispatch.ExecutorAction;
import net.risesoft.api.job.actions.dispatch.executor.DoBalance;
import net.risesoft.api.job.actions.dispatch.executor.Result;
import net.risesoft.api.job.actions.dispatch.executor.ResultError;
import net.risesoft.api.job.actions.dispatch.executor.ResultSuccess;
import org.apache.http.HttpResponse;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description : 用rpc 的方式执行
 * @ClassName JobExecutorAction
 * @Author lb
 * @Date 2022/9/15 11:12
 * @Version 1.0
 */

public abstract class HttpExecutorAction implements ExecutorAction, DisposableBean, InitializingBean {
    /**
     * 默认超时时间 120 秒当没配置超时时间的时候默认120秒
     */
    @Value("${beta.job.timeOut:120}")
    private Integer defaultTimeOut;
    @Autowired
    TaskExecutorService taskExecutorService;

    private static RequestConfig defaultRequestConfig;

    private static CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();

    @Override
    public Result action(Job job, JobLog jobLog, Map<String, Object> args, ServiceInstance iServiceInstance, JobContext jobContext, DoBalance doBalance) {
        String url = iServiceInstance.getUri().toString();
        url += job.getSource();

        return sendTo(url, args, job.getSourceTimeOut() > 0 ? job.getSourceTimeOut() : defaultTimeOut);
    }

    abstract HttpRequestBase getRequest(String url);


    public Result sendTo(String url, Map<String, Object> args, int timeOut) {
        HttpCoreContext context = HttpCoreContext.create();
        args.forEach((k, v) -> {
            context.setAttribute(k, v);
        });
        HttpRequestBase request = getRequest(url);
        if (timeOut == 0) {
            request.setConfig(defaultRequestConfig);
        } else {
            request.setConfig(RequestConfig.custom()
                    .setSocketTimeout(timeOut * 1000)
                    .setConnectTimeout(timeOut * 1000)
                    .setConnectionRequestTimeout(timeOut * 1000)
                    .build());
        }
        HttpRequest httpRequest = new HttpRequest(url);
        httpclient.execute(request, context, httpRequest);
        return httpRequest;
    }


    @Override
    public void destroy() throws Exception {
        if (httpclient.isRunning()) {
            httpclient.close();
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!httpclient.isRunning()) {
            httpclient.start();
            defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(this.defaultTimeOut * 1000)
                    .setConnectTimeout(this.defaultTimeOut * 1000)
                    .setConnectionRequestTimeout(this.defaultTimeOut * 1000)
                    .build();
        }

    }
}

class HttpRequest implements FutureCallback<HttpResponse>, Result {

    private String url;

    public HttpRequest() {
    }

    public HttpRequest(String url) {
        this.url = url;
    }

    @Override
    public void completed(HttpResponse result) {

        String content = null;
        try {
            content = EntityUtils.toString(result.getEntity(), "UTF-8");
        } catch (IOException e) {
            failed(e);
        }
        if (result.getStatusLine().getStatusCode() == 200) {
            for (int i = 0; i < successes.size(); i++) {
                successes.remove(i).onSuccess(content);
                i--;
            }
        } else {
            failed(new JobException(this.url + " error_status: " + result.getStatusLine() + " error" + content));
        }

    }

    @Override
    public void failed(Exception ex) {

        for (int i = 0; i < errors.size(); i++) {
            errors.remove(i).onError(ex);
            i--;
        }
    }

    @Override
    public void cancelled() {
        failed(new JobException("任务被取消"));
    }

    private List<ResultSuccess> successes = new ArrayList<>();
    private List<ResultError> errors = new ArrayList<>();

    @Override
    public Result onSuccess(ResultSuccess success) {

        successes.add(success);
        return this;
    }

    @Override
    public Result onError(ResultError error) {
        errors.add(error);
        return this;
    }

    @Override
    public Object getValue() {
        throw new JobException("no getValue");
    }
}