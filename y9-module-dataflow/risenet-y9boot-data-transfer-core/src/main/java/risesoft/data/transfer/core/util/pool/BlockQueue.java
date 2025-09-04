package risesoft.data.transfer.core.util.pool;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import risesoft.data.transfer.core.exception.FrameworkErrorCode;
import risesoft.data.transfer.core.exception.TransferException;

/**
 * 用blockqueue实现的队列，让add方法和poll方法能够完成等待和阻塞
 * 
 * 
 * @typeName BlockQueue
 * @date 2025年8月15日
 * @author lb
 */
public class BlockQueue<T> extends AbstractQueue<T> implements Queue<T> {

	private LinkedBlockingQueue<T> blockQueue;

	public BlockQueue(int size) {
		this.blockQueue = new LinkedBlockingQueue<T>(size);
	}

	@Override
	public boolean offer(T e) {
		return blockQueue.offer(e);
	}

	@Override
	public   boolean add(T e) {
		try {
			blockQueue.put(e);
		} catch (InterruptedException e1) {
			throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "阻塞队列添加出错", e1);
		}
		return true;
	}

	@Override
	public  T poll() {

		try {
			return blockQueue.take();
		} catch (InterruptedException e) {
			throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "阻塞队列获取出错", e);
		}
	}

	@Override
	public  T peek() {
		return blockQueue.peek();
	}

	@Override
	public  Iterator<T> iterator() {
		return blockQueue.iterator();
	}

	@Override
	public  int size() {
		return blockQueue.size();
	}

}