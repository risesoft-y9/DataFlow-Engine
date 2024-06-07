package net.risedata.jdbc.executor.sync;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ThreadPoolSyncService implements SyncService{
	
	private ExecutorService executor;
	
	

	/**
	 * @param executor
	 */
	public ThreadPoolSyncService(ExecutorService executor) {
		this.executor = executor;
	}



	@Override
	public <T> Future<T> submit(Callable<T> call) {
		return executor.submit(call);
	}

}
