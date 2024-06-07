package net.risedata.rpc.service.patientia;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.risedata.rpc.service.RPCExecutorService;

/**
 * @description: 默认的线程池执行器实现
 * @Author lb176
 * @Date 2021/8/30==14:48
 */
public class FixedExecutorService implements RPCExecutorService {
    /**
     * 执行线程的
     */
    private ThreadPoolExecutor executorService;

    public FixedExecutorService(int size) {
        executorService = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }


    @Override
    public void executor(Runnable task) {
        executorService.execute(task);
    }

    @Override
    public int getMaximumPoolSize() {

        return executorService.getMaximumPoolSize();
    }

    @Override
    public int getActiveCount() {
        return executorService.getActiveCount();
    }

    @Override
    public long getTaskCount() {
        return executorService.getTaskCount();
    }

    @Override
    public int getCorePoolSize() {
        return executorService.getCorePoolSize();
    }

    @Override
    public long getCompletedTaskCount() {
        return executorService.getCompletedTaskCount();
    }

    @Override
    public int getPoolSize() {
        return executorService.getPoolSize();
    }


}
