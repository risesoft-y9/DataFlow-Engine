package net.risedata.jdbc.commons.threads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AwaitFuture<T> implements Future<T> {

	private Future<T> future;

	private StatusedCallable<T> callable;

	/**
	 * @param future
	 */
	public AwaitFuture(Future<T> future, StatusedCallable<T> callable) {
		this.future = future;
		this.callable = callable;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		if (!callable.isStart() && callable.cancel()) {
			future.cancel(false);
			try {
				return this.callable.run();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		return future.get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(timeout, unit);
	}

}
