package net.risedata.rpc.Task;

import org.springframework.beans.factory.annotation.Value;

import net.risedata.rpc.provide.config.Application;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 用来重新连接服务器的任务
 */
public class ScheduleTask extends TimerTask {

    private ArrayList<Task> taskS = new ArrayList<>();
    private Timer timer;

    public void addTask(Task task) {
        synchronized (taskS) {
            taskS.add(task);
        }
    }

    public ScheduleTask(Long refreshTime) {
        this.refreshTime = refreshTime;
    }

    private Long refreshTime;

    public void start() {
        timer = new Timer();
        timer.schedule(this, 0, refreshTime);
        Application.logger.info("  task start refreshTime = " + this.refreshTime);
    }

    @Override
    public synchronized void run() {
        Object[] tasks = null;
        synchronized (taskS) {
            if (taskS.size() > 0) {
                synchronized (taskS) {//任务按批次执行
                    tasks = taskS.toArray();
                    taskS.clear();
                }
            } else {
                return;
            }
        }
        for (Object task : tasks) {
            try {


                ((Task) task).run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void stop() {
        this.taskS.clear();
        timer.cancel();
        this.taskS.clear();
    }

}

