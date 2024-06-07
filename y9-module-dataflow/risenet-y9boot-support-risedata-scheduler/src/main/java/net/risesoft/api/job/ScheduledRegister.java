package net.risesoft.api.job;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @Description : 定时任务注册管理器
 * @ClassName ScheduledRegister
 * @Author lb
 * @Date 2022/9/14 10:52
 * @Version 1.0
 */
@Component
public class ScheduledRegister implements DisposableBean {
    @Autowired
    TaskScheduler taskScheduler;
    /**
     * 缓存任务
     */
    private Map<Runnable, ScheduledFuture> futureMap = new ConcurrentHashMap<>();

    /**
     * 添加一个任务
     * @param task 任务
     * @param trigger 执行时间器
     */
    public void addTriggerTask(Runnable task, Trigger trigger) {
        ScheduledFuture<?> future = taskScheduler.schedule(task, trigger);
        futureMap.put(task,future);

    }

    /**
     * 删除一个任务并停止
     *
     * @param task
     * @return
     */
    public boolean removeTask(Runnable task) {
        ScheduledFuture future = futureMap.remove(task);
        if (future != null) {
            future.cancel(true);
            return true;
        }
        return false;
    }



    @Override
    public void destroy() throws Exception {
        futureMap.forEach((k, v) -> {
            v.cancel(true);
        });
        futureMap.clear();
    }
}
