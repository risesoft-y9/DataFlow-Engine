package risesoft.data.transfer.core.handle.cache;

import java.util.List;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.handle.Handle;
import risesoft.data.transfer.core.plug.RootPlug;

/**
 * 缓存插件
 * 实现这个插件的对象需要实现缓存方法，缓存组件用于任务的部分参数持久化以及提供给更多方法使用
 * 
 * @typeName CacheHandle
 * @date 2025年8月18日
 * @author lb
 */
public interface CacheHandle extends RootPlug, Handle {
	@Override
	default boolean register(JobContext jobContext) {
		return true;
	}

	/**
	 * 根据key获取一个参数
	 * @param key key
	 * @return
	 */
	String get(String key);
	
	/**
	 * 添加一个参数
	 * @param key key
	 * @param value value
	 */
	void put(String key,String value);
 
	/**
	 * 追加一个值，使用这个方法时为数组队列模式
	 * @param key key
	 * @param value value
	 * @return 返回数组大小
	 */
	int push(String key,String value);
	
	/**
	 * 将key中的值出栈 
	 * @param key key
	 * @param size 拉取的数量
	 * @return
	 */
	List<String> poll(String key,int size);

	
	/**
	 * 删除一个key
	 * @param key
	 * @return 是否存在
	 */
	boolean remove(String key);
	
	
}
