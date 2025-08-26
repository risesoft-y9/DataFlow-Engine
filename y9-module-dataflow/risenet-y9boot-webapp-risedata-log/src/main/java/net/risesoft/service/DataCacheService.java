package net.risesoft.service;

import net.risesoft.pojo.Y9Result;

public interface DataCacheService {
	
	/**
	 * 根据key值获取数据
	 * @param key
	 * @return
	 */
	Y9Result<String> get(String key);
	
	/**
	 * 存储数据
	 * @param key
	 * @param value
	 * @return
	 */
	Y9Result<String> put(String key, Object value);
	
	/**
	 * 根据key删除数据
	 * @param key
	 * @return
	 */
	Y9Result<Boolean> remove(String key);
	
	/**
	 * 根据key值往队列里新增数据
	 * @param key
	 * @param value
	 * @return
	 */
	Y9Result<Integer> push(String key, Object value);
	
	/**
	 * 根据key获取队列中指定size大小的数据，获取完成后移除对应队列的值
	 * @param key
	 * @param size
	 * @return
	 */
	Y9Result<String> poll(String key, int size);

}
