package risesoft.data.transfer.base.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.executor.Executor;
import risesoft.data.transfer.core.executor.ExecutorFacotry;
import risesoft.data.transfer.core.executor.ExecutorListener;
import risesoft.data.transfer.core.executor.ExecutorTaskQueue;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.util.CloseUtils;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.pool.ObjectPool;
import risesoft.data.transfer.core.util.pool.SyncObjectPool;

/**
 * 使用当前线程执行任务的执行对接 PS：注意这个类不能放在消费端使用，消费端主线程需要做更多的工作
 * 
 * @typeName ThreadPoolExecutorTaskQueue
 * @date 2023年12月15日
 * @author lb
 */
public class ConcurrentThreadExecutorTaskQueue implements ExecutorTaskQueue {
	/**
	 * 执行器监听器
	 */
	private ExecutorListener executorListener;

	/**
	 * 对象池
	 */
	private ObjectPool<Executor> executorPool;
	/**
	 * 执行器工厂
	 */
	private ExecutorFacotry executor;
	/**
	 * 任务队列
	 */
	private ConcurrentLinkedQueue<Object> linkedQueue = new ConcurrentLinkedQueue();;
	private boolean isStart = false;
	/**
	 * 是否关闭
	 */
	private volatile boolean isShutdown;

	/**
	 * 日志
	 */
	private Logger logger;

	private Object source;
	/**
	 * 对象池的大小，如果超出这个大小则不产生对象并抛出异常
	 */
	private int size;

	public ConcurrentThreadExecutorTaskQueue(Configuration configuration, LoggerFactory loggerFactory) {
		size = configuration.getInt("size", 5);
		logger = loggerFactory.getLogger(configuration.getString("name", "ThreadPoolExecutorTaskQueue"));
		isShutdown = false;
		source = this;
		executorPool = new SyncObjectPool<Executor>(size, () -> {
			if (logger.isDebug()) {
				logger.debug(source, "create executor instance:" + executorPool.getConcurrentSize()
						+ " created instance size:" + size);
			}
			return executor.getInstance();
		});
		if (logger.isInfo()) {
			logger.info(source, "inited max size " + size);
		}
	}

	@Override
	public synchronized void close() throws Exception {
		int size = executorPool.getConcurrentSize();
		logger.info(this, "close:" + executorPool.getConcurrentSize());
		for (int i = 0; i < size; i++) {
			CloseUtils.close(executorPool.getInstance());
		}
		this.executor.close();
		this.isStart = false;
	}

	@Override
	public void add(Object task) {
		if (isStart) {
			linkedQueue.add(task);
			this.runJob();
		} else {
			throw TransferException.as(CommonErrorCode.CONFIG_ERROR, "不支持在未启动的状态下执行任务,请确保您未将此实现类用于生产者,生产者必须是异步的线程!");
		}

	}

	@Override
	public void addBatch(@SuppressWarnings("rawtypes") Collection task) {
		for (Object object : task) {
			add(object);
		}
	}

	@Override
	public Collection<Object> getResidue() {
		return new ArrayList<Object>();
	}

	@Override
	public int getResidueSize() {
		return executorPool.getConcurrentSize();
	}

	@Override
	public void setExecutorFacoty(ExecutorFacotry executor) {
		this.executor = executor;
	}

	@Override
	public void start() {
		this.executorPool.clear();
		executorListener.start();
		this.isStart = true;
	}

	@Override
	public int getExecutorSize() {
		return size;
	}

	@Override
	public void shutdown() throws Exception {
		logger.info(this, "shutdown");
		this.isShutdown = true;
		this.close();

	}

	@Override
	public void setExecutorListener(ExecutorListener executorListener) {
		this.executorListener = executorListener;
	}

	private void runJob() {
		try {
			if (isShutdown) {
				return;
			}
			if (logger.isDebug()) {
				logger.debug(source, "run job: ");
			}

			Executor executor = executorPool.getInstance();
			Object taskObject = linkedQueue.poll();
			executorListener.taskStart(taskObject);
			if (isShutdown) {
				return;
			}
			executor.run(taskObject);
			executorPool.back(executor);
			logger.debug(source, "end job");
			if (linkedQueue.size() == 0) {
				logger.debug(source, "task end");
				executorListener.taskEnd(taskObject);
			}

		} catch (Throwable e) {
			logger.error(source, e.getMessage());
			executorListener.onError(e);
		}

	}

}
