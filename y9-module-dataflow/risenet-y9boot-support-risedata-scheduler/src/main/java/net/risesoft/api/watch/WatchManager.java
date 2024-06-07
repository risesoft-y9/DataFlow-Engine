package net.risesoft.api.watch;

import io.netty.util.HashedWheelTimer;
import net.risedata.register.service.IServiceInstanceFactory;
import net.risesoft.api.persistence.iservice.IServiceService;
import net.risesoft.api.persistence.model.IServiceInstanceModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description :
 * @ClassName WatchManager
 * @Author lb
 * @Date 2021/12/3 17:32
 * @Version 1.0
 */
public class WatchManager implements ApplicationContextAware {

    /**
     * 存储着 instanceid -> task
     */
    private static final Map<String, CheckStatusTask> WATCH_TASK = new ConcurrentHashMap<>();


    private static HashedWheelTimer hashedWheelTimer = new HashedWheelTimer();

    public static final Logger LOGGER = LoggerFactory.getLogger(WatchManager.class);

    /**
     * 定时任务启动时间单位毫秒
     */
    @Value("${beta.service.watch.startTime:100}")
    private Integer startTime;

    @Autowired
    private IServiceService iServiceService;


    @Autowired
    IServiceInstanceFactory iServiceInstanceFactory;

    /**
     * 删除任务
     *
     * @param instanceId
     * @param checkStatusTask
     */
    public static void removeTask(String instanceId, CheckStatusTask checkStatusTask) {
        synchronized (WATCH_TASK) {
            CheckStatusTask statusTask = WATCH_TASK.get(instanceId);
            if (statusTask == checkStatusTask) {
                WATCH_TASK.remove(instanceId);
            }
        }

    }


    /**
     * 查询后加载定时任务 监控 60 秒一次
     */
    @Scheduled(fixedDelayString = "${beta.service.watch.refreshTime:60000}", initialDelayString = "${beta.service.watch.refreshTime:60000}")
    public void onRefresh() {

        List<IServiceInstanceModel> instanceModels = iServiceService.findWatch(iServiceInstanceFactory.getIsntance().getInstanceId(), iServiceInstanceFactory.getIsntance().getEnvironment(), iServiceInstanceFactory.getIsntance().getServiceId());
        for (IServiceInstanceModel instanceModel : instanceModels) {
            iServiceService.updateWatch(iServiceInstanceFactory.getIsntance().getInstanceId(), instanceModel.getWatchServer(), instanceModel.getInstanceId());
        }
        checkTask();
    }

    private void checkTask() {
        Set<String> keySet = WATCH_TASK.keySet();
        String[] ids = keySet.toArray(new String[keySet.size()]);
        iServiceService.updateNoWatch(ids);
        List<String> misss = iServiceService.findMiss(iServiceInstanceFactory.getIsntance().getInstanceId(), ids);
        for (String key : misss) {
            addTask(key);
        }
    }

    /**
     * 确保任务唯一性
     *
     * @param task       任务
     * @param instanceId 实例 id
     * @return
     */
    public static boolean hasTask(CheckStatusTask task, String instanceId) {
        synchronized (WATCH_TASK) {
            return WATCH_TASK.get(instanceId) == task;
        }
    }


    public void addTask(String instanceId) {
        CheckStatusTask checkStatusTask = new CheckStatusTask(instanceId, iServiceInstanceFactory.getIsntance().getInstanceId(), iServiceService);
        synchronized (WATCH_TASK) {
            WATCH_TASK.put(instanceId, checkStatusTask);
        }
        hashedWheelTimer.newTimeout(checkStatusTask, startTime, TimeUnit.MILLISECONDS);
    }


    public static boolean get(String id) {
        return WATCH_TASK.containsKey(id);
    }

    public static Map<String, CheckStatusTask> getWatchTask() {
        return WATCH_TASK;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        hashedWheelTimer = new HashedWheelTimer();
        hashedWheelTimer.start();
    }

    public static void closeApplication() {
        WATCH_TASK.clear();
        hashedWheelTimer.stop();
        LOGGER.info("close watch");
    }
}
