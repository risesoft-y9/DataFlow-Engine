package net.risedata.jdbc.commons.threads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务执行接口
 * 
 * @author lb
 * @date 2023年2月3日 上午11:33:36
 */
public class TaskAwaitExecutor implements ExecutorService {

	private ExecutorService executorService;

	/**
	 * @param executorService
	 */
	public TaskAwaitExecutor(ExecutorService executorService) {
		this.executorService = executorService;
	}



	@Override
	public void execute(Runnable command) {
		executorService.execute(command);

	}

	@Override
	public void shutdown() {
		executorService.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return executorService.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return executorService.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return executorService.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return executorService.awaitTermination(timeout, unit);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		StatusedCallable<T> statusedCallable = new StatusedCallable<>(task);
		Future<T> future = executorService.submit(statusedCallable);
		
		return new AwaitFuture<>(future, statusedCallable);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return submit(new Callable<T>() {

			@Override
			public T call() throws Exception {
				task.run();
				return result;
			}
		});
	}

	@Override
	public Future<?> submit(Runnable task) {
		return submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				task.run();
				return null;
			}

		});
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		List<Future<T>> invokeAll = executorService.invokeAll(tasks);
		List<Future<T>> result = new ArrayList<>();
		for (Callable<T> callable : tasks) {
			result.add(new AwaitFuture<>(invokeAll.get(result.size()), new StatusedCallable<>(callable)));
		}
		return result;
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		return executorService.invokeAll(tasks, timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return executorService.invokeAny(tasks);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return executorService.invokeAny(tasks, timeout, unit);
	}

}
