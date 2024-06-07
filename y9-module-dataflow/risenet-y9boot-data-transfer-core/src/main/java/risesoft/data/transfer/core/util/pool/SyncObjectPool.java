package risesoft.data.transfer.core.util.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.management.RuntimeErrorException;

/**
 * 简单的对象池这个池不处理 等待等问题因为大小都是固定的
 * 
 * @typeName SimpledObjectPool
 * @date 2023年12月15日
 * @author lb
 */
public class SyncObjectPool<T> implements ObjectPool<T> {

	private BlockingQueue<T> queue;

	private volatile int size;
	private int maxSize;

	private Callable<T> createT;

	public SyncObjectPool(int maxSize, Callable<T> createT) {
		this.queue = new LinkedBlockingQueue<T>(maxSize);
		this.size = 0;
		this.maxSize = maxSize;
		this.createT = createT;
	}

	public T getInstance() {
		T ret = queue.poll();
		if (ret == null && size < maxSize) {
			synchronized (this) {
				if (size < maxSize) {
					queue.add(createInstance());
				}
			}
		}
		try {
			return queue.take();
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	public void back(T instance) {
		if (maxSize >= this.queue.size()) {
			this.queue.add(instance);
			return;
		}
		throw new RuntimeException("超出容量" + this.queue.size());
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
		this.queue.clear();
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
		return queue.size();
	}
}
