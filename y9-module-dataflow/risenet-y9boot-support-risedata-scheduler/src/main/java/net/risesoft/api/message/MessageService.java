package net.risesoft.api.message;

import net.risesoft.api.persistence.model.IServiceInstanceModel;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;

/**
 * @Description : 消息服务
 * @ClassName MessageService
 * @Author lb
 * @Date 2022/8/30 10:26
 * @Version 1.0
 */
public interface MessageService  {

    /**
     * 发送消息服务
     * @param recipients 收消息人
     * @param title 标题
     * @param message 短信
     */
    void sendMessage(String[] recipients,String title,String message,Object source);

    /**
     * 服务改变
     * @param serviceInstanceModel
     */
    void onServiceDown(IServiceInstanceModel serviceInstanceModel);

    /**
     * job 出错
     * @param job
     */
    void onJobError(Job job);

    void onJobEnd(Job job, JobLog jobLog);
}
