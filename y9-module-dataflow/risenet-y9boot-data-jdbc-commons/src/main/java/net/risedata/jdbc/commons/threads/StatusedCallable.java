package net.risedata.jdbc.commons.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;


public class StatusedCallable<T> implements Callable<T> {

	private Callable<T> callable;

	/**
	 * @param callable
	 */
	public StatusedCallable(Callable<T> callable) {
		this.callable = callable;
	}

	private AtomicInteger status = new AtomicInteger(0);

	@Override
	public T call() throws Exception {
		if (status.compareAndSet(0, 1)) {
			return callable.call();
		}
		return null;
	}

	public T run() throws Exception{
		 return callable.call();
	}
	public boolean isStart() {
		return status.intValue() == 1;
	}

	public boolean cancel() {
		return status.compareAndSet(0, -1);
	}

}
