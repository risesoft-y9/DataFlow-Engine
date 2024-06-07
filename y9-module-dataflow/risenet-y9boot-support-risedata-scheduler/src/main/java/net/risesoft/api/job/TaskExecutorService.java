package net.risesoft.api.job;

import cn.hutool.core.date.DateUtil;
import net.risedata.register.service.IServiceInstanceFactory;
import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.actions.JobAction;
import net.risesoft.api.job.actions.start.StartJobAction;
import net.risesoft.api.job.actions.start.StartJobChildrenAction;
import net.risesoft.api.message.MessageService;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description : 任务执行器
 * @ClassName TaskExecutor
 * @Author lb
 * @Date 2022/9/13 15:43
 * @Version 1.0
 */
@Component
public class TaskExecutorService implements ApplicationContextAware, DisposableBean {
    public TaskExecutorService() {

    }

    @Autowired
    TaskManager taskManager;

    @Autowired
    JobLogService jobLogService;

    @Autowired
    IServiceInstanceFactory iServiceInstanceFactory;
    @Autowired
    List<JobAction> actions;

    private Map<Class<?>, JobAction> jobActionMap = new ConcurrentHashMap<>();

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void appendLog(String logId, String context) {
        jobLogService.appendLog(logId, context);
    }

    /**
     * 存放着在执行的jobid=>logId
     */
    private Map<Integer, List<String>> jobLogMap = new ConcurrentHashMap<>();


    private synchronized void addLog(Integer jobId, String logId) {
        List<String> logs = jobLogMap.get(jobId);
        if (logs == null) {
            logs = new ArrayList<>();
            jobLogMap.put(jobId, logs);
        }
        logs.add(logId);
    }

    private synchronized void removeLog(Integer jobId, String logId) {
        List<String> logs = jobLogMap.get(jobId);
        if (logs != null) {
            logs.remove(logId);
            if (logs.size() == 0) {
                jobLogMap.remove(jobId);
            }
        }
    }

    public synchronized List<String> getLogs(Integer jobId) {
        return jobLogMap.get(jobId);

    }

    public static final String SERVER_KEY = "$server";

    public String startJob(Job job) {
        return startJob(job,null);
    }

    /**
     * 启动任务
     *
     * @param job 任务
     */
    public String startJob(Job job, String server) {
        //构建任务日志
        JobLog jobLog = new JobLog();
        jobLog.setStatus(0);
        jobLog.setEnvironment(job.getEnvironment());
        jobLog.setChildJobId(job.getChildJobs());
        jobLog.setDispatchSource("");
        jobLog.setJobId(job.getId());
        jobLog.setDispatchServer(iServiceInstanceFactory.getIsntance().getInstanceId());
        jobLog.setLogConsole(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " start job \n");
        jobLogService.saveLog(jobLog);
        addLog(job.getId(), jobLog.getId());
        HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.isEmpty(server)) {
            map.put("$server", server);
        }
        try {
            toAction(StartJobAction.class, job, jobLog, new JobContext(map));
        } catch (Exception e) {
            jobLog.setStatus(2);
            jobLogService.updateStatus(jobLog.getId(), 2);
            removeLog(job.getId(), jobLog.getId());
            jobLogService.appendLog(jobLog.getId(), "执行任务失败异常信息:\n" + getExceptionStack(e).substring(0,250));
        }
        return jobLog.getId();
    }


    public void startJob(Job job, JobLog jobLog, JobContext jobContext) {
        if (jobContext == null) {
            jobContext = new JobContext(new HashMap<>());
        }
        try {
            jobLogService.updateStatus(jobLog.getId(), JobLog.START, jobLog.getStatus());
            jobLog.setStatus(JobLog.START);
            addLog(job.getId(), jobLog.getId());
            toAction(StartJobAction.class, job, jobLog, jobContext);
        } catch (Exception e) {
            this.endJob(job, jobLog, JobLog.ERROR, "执行任务失败异常信息:\n" + e.getMessage(), jobContext);
        }
    }

    public static String getExceptionStack(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        throwable.printStackTrace(pw);
        return sw.toString();
    }

    @Autowired
    MessageService messageService;

    /**
     * 结束任务
     *
     * @param jobLog 日志
     * @param status 状态
     * @param msg    消息
     */
    public void endJob(Job job, JobLog jobLog, Integer status, String msg, String result, JobContext jobContext) {
        if (jobContext.isChildrenJob()) {
            appendLog(jobLog.getId(), new StringBuilder("任务<<").append(job.getId()+job.getName()+"("+job.getDescription()+")").append(">>执行结束\n执行状态:"
                    +(status==1?"成功":"失败")  + "\n消息:" + msg + "\n返回值:" + result).toString());
            if (status==2){
                closeJob(job,jobLog,status,msg,result,jobContext);
            }
        } else {
            closeJob(job,jobLog,status,msg,result,jobContext);
        }

    }
    
    private  void closeJob(Job job, JobLog jobLog, Integer status, String msg, String result, JobContext jobContext) {
        jobLogService.endJob(jobLog.getId(), status, msg, result, job.getEnvironment());
        taskManager.endJob(job, jobLog.getJobId());
        removeLog(job.getId(), jobLog.getId());
        if (status == JobLog.ERROR) {
            messageService.onJobError(job);
        }
        messageService.onJobEnd(job,jobLog);
    }

    /**
     * 结束任务
     *
     * @param jobLog 日志
     * @param status 状态
     * @param msg    消息
     */
    public void endJob(Job job, JobLog jobLog, Integer status, String msg, JobContext jobContext) {
        endJob(job, jobLog, status, msg, msg, jobContext);
    }

    /**
     * 结束任务
     *
     * @param jobLog 日志
     * @param msg    消息
     */
    public void successJob(Job job, JobLog jobLog, String msg, String result, JobContext jobContext) {
        if (!StringUtils.isEmpty(job.getChildJobs())) {
            try {
                toAction(StartJobChildrenAction.class, job, jobLog, jobContext).onEnd((success) -> {
                    endJob(job, jobLog, JobLog.SUCCESS, msg, result, jobContext);
                });
            }catch ( Exception e){
                endJob(job, jobLog, JobLog.ERROR, "子任务执行失败系统异常:"+e.getMessage(), result, jobContext);

            }

        } else {
            endJob(job, jobLog, JobLog.SUCCESS, msg, result, jobContext);
        }
    }

    public LResult toAction(Class<? extends JobAction> actionClass, Job job, JobLog jobLog, JobContext jobContext) {
        JobAction jobAction = jobActionMap.get(actionClass);
        if (jobAction == null) {
            throw new JobException("不存在的步骤:" + actionClass);
        }
        return jobAction.action(job, jobLog, this, jobContext);
    }

    public <T> T getAction(Class<T> actionClass) {
        JobAction jobAction = jobActionMap.get(actionClass);
        if (jobAction == null) {
            throw new JobException("不存在的步骤:" + actionClass);
        }
        return actionClass.cast(jobAction);
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        for (JobAction action : actions) {
            jobActionMap.put(action.getClass(), action);
        }
    }

    /**
     * 添加调度源
     *
     * @param id
     * @param source
     */
    public void appendSource(String id, String source, String msg) {
        jobLogService.appendSource(id, source, msg);
    }

    @Override
    public void destroy() throws Exception {
        jobLogMap.clear();
    }
}
