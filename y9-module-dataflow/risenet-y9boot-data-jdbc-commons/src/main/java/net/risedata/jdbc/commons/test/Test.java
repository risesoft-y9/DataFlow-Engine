package net.risedata.jdbc.commons.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class Test {

	public static long testOne(Runnable runnable, int count) {
		long time = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			runnable.run();
		}
		long endTime = System.currentTimeMillis() - time;
		System.out.println(String.format("总耗时: %s \n 平均耗时: %s", endTime, endTime / count));
		return endTime;
	}

	public static long test(Runnable runnable, int count, int threadCount) {
		AtomicLong long1 = new AtomicLong();
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		for (int i = 0; i < threadCount; i++) {
			new Thread(() -> {
				long1.addAndGet(testOne(runnable, threadCount));
				countDownLatch.countDown();
			}).start();
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(long1);
		return long1.get();
	}

}
