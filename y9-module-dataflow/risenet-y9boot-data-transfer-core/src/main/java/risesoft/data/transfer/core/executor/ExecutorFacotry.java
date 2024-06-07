package risesoft.data.transfer.core.executor;
/**
 * 执行器创造工厂
 * @typeName ExecutorFacotry
 * @date 2023年12月8日
 * @author lb
 * @param <T>
 */
public interface ExecutorFacotry {
	/**
	 * 执行实例
	 * 
	 * @return
	 */
	Executor getInstance();

	/**
	 * 这个close 代表不需要使用了
	 */
	void close();

}
