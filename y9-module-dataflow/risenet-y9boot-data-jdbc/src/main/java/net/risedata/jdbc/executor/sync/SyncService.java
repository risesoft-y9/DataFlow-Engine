package net.risedata.jdbc.executor.sync;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface SyncService {
	
	<T> Future<T> submit(Callable<T> call);
}
