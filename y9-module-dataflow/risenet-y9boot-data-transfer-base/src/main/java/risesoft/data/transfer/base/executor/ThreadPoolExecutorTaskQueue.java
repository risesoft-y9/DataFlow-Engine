package risesoft.data.transfer.base.executor;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import risesoft.data.transfer.core.executor.Executor;
import risesoft.data.transfer.core.executor.ExecutorFacotry;
import risesoft.data.transfer.core.executor.ExecutorListener;
import risesoft.data.transfer.core.executor.ExecutorTaskQueue;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.util.CloseUtils;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.pool.SimpledObjectPool;

/**
 * 线程池实现的任务队列执行器
 * 
 * @typeName ThreadPoolExecutorTaskQueue
 * @date 2023年12月15日
 * @author lb
 */
public class ThreadPoolExecutorTaskQueue implements ExecutorTaskQueue {
	/**
	 * 执行器监听器
	 */
	private ExecutorListener executorListener;
	/**
	 * 执行器大小
	 */
	private int size;
	/**
	 * 对象池
	 */
	private SimpledObjectPool<Executor> executorPool;
	/**
	 * 执行器工厂
	 */
	private ExecutorFacotry executor;
	/**
	 * 执行器
	 */
	private ThreadPoolExecutor executorService;
	/**
	 * 任务队列
	 */
	private ConcurrentLinkedQueue<Object> linkedQueue;

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

	public ThreadPoolExecutorTaskQueue(Configuration configuration, LoggerFactory loggerFactory) {
		size = configuration.getInt("size", 5);
		executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(size);
		logger = loggerFactory.getLogger(configuration.getString("name", "ThreadPoolExecutorTaskQueue"));
		linkedQueue = new ConcurrentLinkedQueue<Object>();
		isShutdown = false;
		source = this;
		executorPool = new SimpledObjectPool<Executor>(size, () -> {
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
		linkedQueue.add(task);
		if (isStart) {
			this.runJob();
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
		return linkedQueue;
	}

	@Override
	public int getResidueSize() {
		return linkedQueue.size();
	}

	@Override
	public void setExecutorFacoty(ExecutorFacotry executor) {
		this.executor = executor;
	}

	@Override
	public void start() {
		this.executorPool.clear();
		executorListener.start();
		if (logger.isDebug()) {
			logger.debug(source, "start job " + this.linkedQueue.size());
		}
		this.isStart = true;
		if (this.linkedQueue.size() == 0) {
			executorListener.taskEnd(this);
			return;
		}
		for (int i = 0; i < this.linkedQueue.size(); i++) {
			this.runJob();
		}

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
		this.executorService.shutdownNow();

	}

	@Override
	public void setExecutorListener(ExecutorListener executorListener) {
		this.executorListener = executorListener;
	}

	private void runJob() {
		executorService.execute(() -> {
			try {
				if (isShutdown) {
					return;
				}
				if (logger.isDebug()) {
					logger.debug(source, "run job: " + linkedQueue.size());
				}
				Executor executor = executorPool.getInstance();
				Object taskObject = linkedQueue.poll();
				executorListener.taskStart(taskObject);
				executor.run(taskObject);
				// 执行任务后回收对象
				executorPool.back(executor);
				logger.debug(source, "end job");
				if (executorService.getActiveCount() == 1 && linkedQueue.size() == 0) {
					logger.debug(source, "task end");
					executorListener.taskEnd(taskObject);
				}

			} catch (Throwable e) {
				logger.error(source, e.getMessage());
				executorListener.onError(e);
			}
		});
	}

}
