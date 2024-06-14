package net.risesoft.config;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HomeDataThreadPoolConfig {

    RejectedExecutionHandler queueTimeoutHandler = (r, executor) -> {
        try {
            boolean offered = executor.getQueue().offer(r, 2, TimeUnit.SECONDS);
            if (!offered) {
                // 快速失败
                throw new RejectedExecutionException("任务添加到队列超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new RejectedExecutionException("任务添加到队列异常");
        }
    };

    private static final int cpuNums = Runtime.getRuntime().availableProcessors();
    private static final int corePoolSize = cpuNums * 2 + 1;
    private static final int maxCorePoolSize = cpuNums * 5;

    @Bean(name = "homeDataExecutor")
    public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxCorePoolSize);
        threadPoolTaskExecutor.setQueueCapacity(400);
//		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());//异常
        threadPoolTaskExecutor.setRejectedExecutionHandler(queueTimeoutHandler);
        threadPoolTaskExecutor.setThreadNamePrefix("homeData-");
        threadPoolTaskExecutor.setKeepAliveSeconds(300);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
