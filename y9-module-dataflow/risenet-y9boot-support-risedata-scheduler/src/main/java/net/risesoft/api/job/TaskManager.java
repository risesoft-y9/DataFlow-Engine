package net.risesoft.api.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import net.risedata.register.service.IServiceInstanceFactory;
import net.risedata.rpc.consumer.annotation.Listener;
import net.risedata.rpc.consumer.annotation.Listeners;
import net.risedata.rpc.provide.config.Application;
import net.risesoft.api.listener.ClientListener;
import net.risesoft.api.persistence.job.JobChangeService;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.job.JobService;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.persistence.model.log.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description : 任务管理器
 * @ClassName TaskManager
 * @Author lb
 * @Date 2022/9/13 15:44
 * @Version 1.0
 */
@Component
@Listeners
public class TaskManager {
    @Autowired
    private JobService jobService;

    @Autowired
    private IServiceInstanceFactory iServiceInstanceFactory;

    @Autowired
    private TaskExecutorService taskExecutor;

    /**
     * 存储着 job_id -> job任务
     */
    private static final Map<Integer, JobTask> JOB_TASK = new ConcurrentHashMap<>();

    public TaskExecutorService getTaskExecutor() {
        return taskExecutor;
    }

    @Autowired
    JobLogService jobLogService;

    public boolean isBlock(Job job, String logId) {
        return jobLogService.isBlock(job.getId(), job.getTimeOut(), logId);
    }


    /**
     * 添加到待执行的任务管理器
     *
     * @param job    任务
     * @param jobLog 任务日志
     */
    public synchronized void pushTask(Job job, JobLog jobLog) {

        jobLogService.updateStatus(jobLog.getId(), JobLog.AWAIT, jobLog.getStatus());
        if (!isBlock(job, jobLog.getId())) {//当没有阻塞的时候
            pollJob(job, job.getId());
        }

    }

    /**
     * 查询后加载定时任务 监控 30 秒一次
     */
    @Scheduled(fixedDelayString = "${beta.job.refreshTime:30000}", initialDelayString = "${beta.job.refreshTime:30000}")
    public void onRefresh() {
        //获取需要监控的任务
        List<Job> jobs = jobService.findWatch(iServiceInstanceFactory.getIsntance().getInstanceId(), iServiceInstanceFactory.getIsntance().getEnvironment(), iServiceInstanceFactory.getIsntance().getServiceId());
        for (Job job : jobs) {
            if (jobService.updateWatch(job.getId(), iServiceInstanceFactory.getIsntance().getInstanceId(), job.getDispatchServer())) {
                addTask(job);
            }
        }
        checkJob();

    }

    /**
     * 清理30天前的数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearLog() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONDAY, -1);
        jobLogService.clearLog(calendar.getTime());
    }


    @Autowired
    private JobChangeService jobChangeService;
    @Autowired
    private ScheduledRegister scheduledRegister;

    private void refreshChangeJob() {
        List<Integer> changeJobs = jobChangeService.searchChangeJobs();
        if (changeJobs.size() != 0) {
            JobTask jobTask = null;
            for (Integer jobId : changeJobs) {
                jobTask = JOB_TASK.get(jobId);
                if (jobTask != null) {
                    jobTask.onChange(jobService.findByJobId(jobId));
                    jobChangeService.delete(jobId);
                }
            }

        }

    }

    @Scheduled(fixedDelayString = "${beta.job.clearTime:60000}", initialDelayString = "${beta.job.clearTime:60000}")
    public void clearTimeAndDelete() {
        Set<Integer> ids = JOB_TASK.keySet();
        if (ids.size() > 0) {
            jobLogService.clearTimeOutJob();
        }

    }

    private void checkJob() {
        Set<Integer> keySet = JOB_TASK.keySet();
        Integer[] ids = keySet.toArray(new Integer[keySet.size()]);
        if (keySet.size() != 0) {
            refreshChangeJob();
            jobService.updateNoWatch(ids);
        }
        List<Job> misss = jobService.findMiss(iServiceInstanceFactory.getIsntance().getInstanceId(),ids,JOB_TASK);
        for (Job job : misss) {
            addTask(job);
        }

    }

    public void putInfo(Integer jobId, String key, Object value) {
        JobTask jobTask = JOB_TASK.get(jobId);
        if (jobTask != null) {
            jobTask.putInfo(key, value);
        }
    }

    public <T> T getInfo(Integer jobId, String key, Class<T> cla) {
        JobTask jobTask = JOB_TASK.get(jobId);
        if (jobTask != null) {
            Object info = jobTask.getInfo(key);
            return info != null ? cla.cast(jobTask.getInfo(key)) : null;
        }
        return null;
    }

    /**
     * 删除任务
     *
     * @param jobId
     * @param jobTask
     */
    public void removeJob(Integer jobId, JobTask jobTask) {
        JOB_TASK.remove(jobId);
        scheduledRegister.removeTask(jobTask);
    }

    /**
     * 刷新任务
     *
     * @param jobTask
     */
    public void refreshJob(JobTask jobTask) {
        scheduledRegister.removeTask(jobTask);
        scheduledRegister.addTriggerTask(jobTask, jobTask);
    }

    public void addTask(Job job) {
        JobTask task = JOB_TASK.get(job.getId());
        if (task != null) {
            task.onChange(job);
            return;
        }
        JobTask jobTask = new JobTask(job, this);
        JOB_TASK.put(job.getId(), jobTask);
        try {
            scheduledRegister.addTriggerTask(jobTask, jobTask);
        } catch (Exception e) {
            //任务定时报错修改状态并且抛出异常
            e.printStackTrace();
            Application.logger.error("任务添加失败已停止任务任务信息: " + job);
            jobService.setStatus(job.getId(), 0);
            JOB_TASK.remove(job.getId());
            return;
        }
        List<String> logs = taskExecutor.getLogs(job.getId());
        String blockingStrategy = job.getBlockingStrategy();
        if (blockingStrategy.equals("串行")) {
            List<JobLog> jobLogs = jobLogService.findDownJobs(job.getId(), logs);
            if (jobLogs.size() > 0) {
                for (JobLog jobLog : jobLogs) {
                    taskExecutor.appendLog(jobLog.getId(), "未知原因任务掉线重新调度:");
                    taskExecutor.startJob(job, jobLog, null);
                }
            }
        } else {
            JobLog jobLog1 = jobLogService.findDownJob(job.getId(), logs);
            if (jobLog1 != null) {
                taskExecutor.appendLog(jobLog1.getId(), "未知原因任务掉线重新调度:");
                taskExecutor.startJob(job, jobLog1, null);
            }
        }


    }

    public static final String ON_JOB_CHANGE = "ON_JOB_CHANGE";


    /**
     * 发送改变事件
     *
     * @param id
     */
    public void pushJobChange(Integer id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        onChangeJob(id);
        ClientListener.pushListener(ON_JOB_CHANGE, map,
                iServiceInstanceFactory.getIsntance().getServiceId(), null, null);
    }

    /**
     * 当任务更新的时候
     *
     * @param id
     */
    @Listener(ON_JOB_CHANGE)
    public void onChangeJob(Integer id) {
        Job job = jobService.findByJobIdAndUse(id);
        if (job != null) {
            addTask(job);
        }
    }

    /**
     * 判断任务是否需要执行
     *
     * @param id
     * @return
     */
    public boolean hasTask(Integer id) {
        return jobService.hasTask(id);
    }

    /**
     * 获取最早的等待中的任务进行执行
     *
     * @param jobId
     */
    public void endJob(Job job, Integer jobId) {
        pollJob(job, jobId);
    }

    public void pollJob(Job job, Integer jobId) {
        synchronized (jobId.toString().intern()) {
            JobLog jobLog = jobLogService.pollJob(jobId, job.getTimeOut());
            if (jobLog != null) {
                taskExecutor.appendLog(jobLog.getId(), "重新执行此任务-----" + job);
                taskExecutor.startJob(job, jobLog, null);
            }
        }

    }
}
