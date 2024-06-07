package net.risesoft.api.utils;

import net.risesoft.api.job.actions.dispatch.executor.Result;
import net.risesoft.api.job.actions.dispatch.executor.ResultError;
import net.risesoft.api.job.actions.dispatch.executor.ResultSuccess;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description : 异步返回result
 * @ClassName ResultUtils
 * @Author lb
 * @Date 2022/9/19 9:12
 * @Version 1.0
 */
public class ResultUtils {

//这个result 只是为了监听是否调度结束
	public static LResult batchResult(LResult... results) {
		LResult lResult = new LResult();

		AtomicInteger size = new AtomicInteger(results.length);
		Object[] res = new Object[results.length];
		for (int i = 0; i < results.length; i++) {
			final int index = i;
			results[i].onEnd((success) -> {
				res[index] = success;
				if (size.decrementAndGet() == 0) {
					lResult.end(res);
				}
			});

		}
		return lResult;
	}

	public static LResult batchResult(Result... results) {
		LResult lResult = new LResult();

		AtomicInteger size = new AtomicInteger(results.length);
		Object[] res = new Object[results.length];
		for (int i = 0; i < results.length; i++) {
			final int index = i;
			results[i].onSuccess((success) -> {
				res[index] = success;
				if (size.decrementAndGet() == 0) {
					lResult.end(res);
				}
			}).onError((err) -> {
				res[index] = err;
				if (size.decrementAndGet() == 0) {
					lResult.end(res);
				}
			});

		}
		return lResult;
	}

}
