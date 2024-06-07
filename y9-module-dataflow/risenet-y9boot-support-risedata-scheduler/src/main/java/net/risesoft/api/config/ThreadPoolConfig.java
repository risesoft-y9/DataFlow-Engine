package net.risesoft.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig implements SchedulingConfigurer {

    /** 获取当前系统的CPU 数目*/
    static int cpuNums = Runtime.getRuntime().availableProcessors();
    /** 线程池核心池的大小*/
    private static int corePoolSize = cpuNums*2+1;
    /** 线程池的最大线程数*/
    private static int maximumPoolSize = cpuNums * 5;

    /**
     * @Primary 优先使用该全局配置线程池
     * 如果不加@primary @async注解默认采用SimpleAsyncTaskExecutor
     * 不加@primary 可使用@async("threadPoolTaskExecutor")指定线程池
     */
    @Primary
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        /** 核心线程数，默认为1 **/
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        /** 最大线程数，默认为Integer.MAX_VALUE **/
        threadPoolTaskExecutor.setMaxPoolSize(maximumPoolSize);
        /** 队列最大长度，一般需要设置值: 大于等于notifyScheduledMainExecutor.maxNum；默认为Integer.MAX_VALUE **/
        threadPoolTaskExecutor.setQueueCapacity(50);
        /** 线程池维护线程所允许的空闲时间，默认为60s **/
        threadPoolTaskExecutor.setKeepAliveSeconds(60);

        /**
         * 线程池对拒绝任务（无线程可用）的处理策略，目前只支持AbortPolicy、CallerRunsPolicy；默认为后者
         *
         * AbortPolicy:直接抛出java.util.concurrent.RejectedExecutionException异常
         * CallerRunsPolicy:主线程直接执行该任务，执行完之后尝试添加下一个任务到线程池中，可以有效降低向线程池内添加任务的速度
         * DiscardOldestPolicy:抛弃旧的任务、暂不支持；会导致被丢弃的任务无法再次被执行
         * DiscardPolicy:抛弃当前任务、暂不支持；会导致被丢弃的任务无法再次被执行
         */
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setThreadNamePrefix("task--thread");
        //执行初始化会自动执行afterPropertiesSet()初始化
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(corePoolSize);
        // 是否将取消后的任务，从队列中删除
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(setTaskExecutors());

    }

    @Bean(destroyMethod="shutdown")
    public Executor setTaskExecutors(){
        return Executors.newScheduledThreadPool(corePoolSize);
    }
}