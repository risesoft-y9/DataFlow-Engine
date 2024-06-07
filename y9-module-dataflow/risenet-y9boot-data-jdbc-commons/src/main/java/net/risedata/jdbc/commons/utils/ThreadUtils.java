package net.risedata.jdbc.commons.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
/**
 *  线程工具类
 * @author libo
 *2020年11月16日
 */
public class ThreadUtils {
    public static Executor  executor =   Executors.newCachedThreadPool();
    /**
     * 同时执行多条线程最后结束
     * @param runnables
     */
	public static void execution(Runnable ...runnables) {
		CountDownLatch countDownLatch = new CountDownLatch(runnables.length);
		for (Runnable runnable : runnables) {			
			executor.execute(()->{
				try {
					runnable.run();
				} finally {
					countDownLatch.countDown();
				} 
			});
		}
		 try {
	            countDownLatch.await();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
    }
	public static void execution(Collection<Runnable> runnables) {
		CountDownLatch countDownLatch = new CountDownLatch(runnables.size());
		for (Runnable runnable : runnables) {			
			executor.execute(()->{
				 runnable.run();
				 countDownLatch.countDown();
			});
		}
		 try {
	            countDownLatch.await();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
    }
	
}
