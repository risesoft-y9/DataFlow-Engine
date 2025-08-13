package net.risesoft.api.message.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.risesoft.api.listener.JobListener;
import net.risesoft.api.message.InstanceMessage;
import net.risesoft.api.message.JobMessage;
import net.risesoft.api.message.MessageService;
import net.risesoft.api.message.MessageServiceExecutor;
import net.risesoft.api.persistence.model.IServiceInstanceModel;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;

import java.util.List;

/**
 * @Description : 消息发送服务器
 * @ClassName DefaultMessageService
 * @Author lb
 * @Date 2022/8/30 15:01
 * @Version 1.0
 */
@Service
public class DefaultMessageService implements MessageService {

    @Autowired(required = false)
    List<MessageServiceExecutor> serviceExecutors;
    @Autowired(required = false)
    List<JobListener> jobListeners;

    @Value("${beta.job.contacts:}")
    String contacts;

    @Autowired
    InstanceMessage instanceMessage;

    @Autowired
    JobMessage jobMessage;

    @Override
    public void sendMessage(String[] recipients, String title, String message, Object source) {
        if (serviceExecutors==null) {
			return;
		}
        for (MessageServiceExecutor serviceExecutor : serviceExecutors) {
            serviceExecutor.sendMessage(recipients, title, message, source);
        }
    }

    @Override
    public void onServiceDown(IServiceInstanceModel serviceInstanceModel) {
        String message = instanceMessage.createContent(serviceInstanceModel);
        String title = instanceMessage.createTitle(serviceInstanceModel);
        sendMessage(serviceInstanceModel.getManagerInfo() != null ? serviceInstanceModel.getManagerInfo().split(",") : new String[]{}, title, message, serviceInstanceModel);

    }

    @Override
    public void onJobError(Job job) {
        String message = jobMessage.createContent(job);
        String title = jobMessage.createTitle(job);
        sendMessage(!StringUtils.isEmpty(job.getEmail()) ? job.getEmail().split(",") : !StringUtils.isEmpty(contacts) ? contacts.split(",") : new String[]{}, title, message, job);
    }

    @Override
    public void onJobEnd(Job job, JobLog jobLog) {
        if (jobListeners!=null){
            for (JobListener jobListener : jobListeners) {
                jobListener.endJob(job,jobLog);
            }
        }

    }
}
