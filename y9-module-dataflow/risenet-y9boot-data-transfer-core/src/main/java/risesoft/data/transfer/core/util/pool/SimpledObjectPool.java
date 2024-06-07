package risesoft.data.transfer.core.util.pool;

import java.util.concurrent.Callable;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 简单的对象池这个池不处理 等待等问题因为大小都是固定的
 * 
 * @typeName SimpledObjectPool
 * @date 2023年12月15日
 * @author lb
 */
public class SimpledObjectPool<T> implements ObjectPool<T>{

	private ConcurrentLinkedQueue<T> queues;

	private volatile int size;
	private int maxSize;

	private Callable<T> createT;

	public SimpledObjectPool(int maxSize, Callable<T> createT) {
		this.queues = new ConcurrentLinkedQueue<T>();
		this.size = 0;
		this.maxSize = maxSize;
		this.createT = createT;
	}

	public T getInstance() {
		T ret = queues.poll();
		if (ret == null && size < maxSize) {
			synchronized (this) {
				if (size < maxSize) {
					return createInstance();
				}
			}
			ret = queues.poll();
		}
		// TODO 此处不应该报错应该当前线程等待 等待back 对象
		if (ret == null) {
			throw new RuntimeException("没有可用对象:" + size + "maxSize:" + maxSize + " pool:" + this.getConcurrentSize());
		}
		return ret;
	}

	public void back(T instance) {
		if (maxSize >= this.queues.size()) {
			this.queues.add(instance);
			return;

		}
		throw new RuntimeException("超出容量" + this.queues.size());
	}

	private synchronized T createInstance() {
		if (size < maxSize) {
			size++;
			try {
				return this.createT.call();
			} catch (Exception e) {
				throw new RuntimeException("生产对象报错" + e.getMessage());
			}
		}
		throw new RuntimeException("队列不能超过" + maxSize);
	}

	public synchronized void clear() {
		this.size = 0;
		this.queues.clear();
	}

	public int getInstanceSize() {
		return size;
	}

	/**
	 * 获取当前剩余的
	 * 
	 * @return
	 */
	public int getConcurrentSize() {
		return queues.size();
	}
}
