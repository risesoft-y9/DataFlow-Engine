package risesoft.data.transfer.core.executor;

/**
 * 抽象处理泛型
 * 
 * @typeName AbstractExecutor
 * @date 2023年12月11日
 * @author lb
 * @param <T>
 */
public abstract class AbstractExecutor<T> implements Executor {

	@SuppressWarnings("unchecked")
	@Override
	public void run(Object task) {
		runExecutor((T) task);
	}

	public abstract void runExecutor(T task);

}
