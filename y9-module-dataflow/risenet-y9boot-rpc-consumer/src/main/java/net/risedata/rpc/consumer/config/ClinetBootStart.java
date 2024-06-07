package net.risedata.rpc.consumer.config;

import net.risedata.rpc.Task.ScheduleTask;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.core.impl.ClinetBootStrap;
import net.risedata.rpc.consumer.core.impl.HostAndPortConnectionManager;

/**
 * @description: 非SpringBoot 的快速启动器
 * @Author lb176
 * @Date 2021/4/29==16:06
 */
public class ClinetBootStart {

    private static ClinetBootStrap clinetBootStrap;
    private static ScheduleTask scheduleTask;

    public static void initConfig() {
        clinetBootStrap = new ClinetBootStrap();
        clinetBootStrap.start(10);
        scheduleTask = new ScheduleTask(5000L);
        scheduleTask.start();
    }

    public static ConnectionManager getConnectionManager() {
        return new HostAndPortConnectionManager(clinetBootStrap, scheduleTask);
    }
}
