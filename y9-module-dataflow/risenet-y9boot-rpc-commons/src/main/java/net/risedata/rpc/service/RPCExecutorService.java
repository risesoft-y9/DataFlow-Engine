package net.risedata.rpc.service;


/**
 * @description: 执行远程调用的线程池service接口
 * @Author lb176
 * @Date 2021/8/30==14:40
 */
public interface RPCExecutorService {
    /**
     * 执行一条任务
     *
     * @param task 任务
     */
    void executor(Runnable task);

    /**
     * 获取最大执行数
     * @return
     */
    int getMaximumPoolSize();

    /**
     * 获取当前激活数
     * @return
     */
    int getActiveCount();

    /**
     * 获取任务数
     * @return
     */
    long getTaskCount();

    /**
     * 获取核心线程数
     * @return
     */
    int getCorePoolSize();

    /**
     * 获取总共完成 的任务数量
     * @return
     */
    long getCompletedTaskCount();

    /**
     * 获取当前线程数
     * @return
     */
    int getPoolSize();

}
