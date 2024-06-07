package risesoft.data.transfer.core.util.pool;

/**
 * 对象池，用于缓存对象创建对象
 * 
 * @typeName ObjectPool
 * @date 2024年3月8日
 * @author lb
 * @param <T>
 */
public interface ObjectPool<T> {
	/**
	 * 获取一个缓存实例
	 * 
	 * @return
	 */
	T getInstance();

	/**
	 * 实例用完后返回给池
	 * 
	 * @param instance
	 */
	void back(T instance);

	/**
	 * 清空实例
	 */
	void clear();

	/**
	 * 获取当前有多少个实例
	 * 
	 * @return
	 */
	int getInstanceSize();

	/**
	 * 获取当前可用实例
	 * 
	 * @return
	 */
	int getConcurrentSize();
}
