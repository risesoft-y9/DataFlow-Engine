package net.risedata.rpc.consumer.task;

import net.risedata.rpc.Task.Task;
import net.risedata.rpc.consumer.config.ConsumerApplication;

/**
 * @description: 达到一定次数才执行的任务
 * @Author lb176
 * @Date 2021/4/29==15:18
 */
public class RepetitionedTask implements Task {

    private int count = 0;
    private int max = 0;
    private Task task;

    public RepetitionedTask(int max, Task task) {
        this.max = max;
        this.task = task;
    }

    @Override
    public void run() {
        try {
            if (count == max) {
                count = 0;
                task.run();
            } else {
                count++;
            }
        } finally {
            if (ConsumerApplication.scheduleTask != null) {
                ConsumerApplication.scheduleTask.addTask(this);
            }

        }
    }
}
