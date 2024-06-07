package net.risesoft.config;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class HomeDataThreadPoolConfig {

	public final ThreadFactory threadFactory = new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setName("homeData-%d");
			return thread;
		}

	};

//	 @Bean(name = "homeDataExecutor")
//	  public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
//	    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//	    taskExecutor.setThreadFactory(threadFactory);
//	    // 核心线程数
//	    taskExecutor.setCorePoolSize(8);
//	    // 最大线程数
//	    taskExecutor.setMaxPoolSize(16);
//	    // 阻塞队列长度
//	    taskExecutor.setQueueCapacity(100);
//	    // 空闲线程最大存活时间
//	    taskExecutor.setKeepAliveSeconds(200);
//	    // 拒绝策略
//	    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//	    taskExecutor.initialize();
//	    return taskExecutor;
//	  }

	private static int cpuNums = Runtime.getRuntime().availableProcessors();
	private static int corePoolSize = cpuNums + 1;
	private static int maxCorePoolSize = cpuNums * 2;

	@Bean(name = "homeDataExecutor")
	public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(9);
		threadPoolTaskExecutor.setMaxPoolSize(20);
		threadPoolTaskExecutor.setQueueCapacity(100);
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		threadPoolTaskExecutor.setThreadNamePrefix("homeData-");
		threadPoolTaskExecutor.initialize();
		return threadPoolTaskExecutor;
	}
}
