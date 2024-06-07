package net.risesoft.api.message.impl;

import cn.hutool.core.date.DateUtil;
import net.risesoft.api.message.InstanceMessage;
import net.risesoft.api.message.JobMessage;
import net.risesoft.api.persistence.model.IServiceInstanceModel;
import net.risesoft.api.persistence.model.job.Job;

import java.util.Date;

/**
 * @Description : 默认的instance 消息创建器
 * @ClassName DefaultInstanceMessage
 * @Author lb
 * @Date 2022/8/30 15:05
 * @Version 1.0
 */
public class DefaultJobMessage implements JobMessage {
    @Override
    public String createContent(Job job) {
        return "定时任务:" + job.getDescription() + "于" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "执行失败!";
    }

    @Override
    public String createTitle(Job job) {
        return job.getDescription();
    }
}
