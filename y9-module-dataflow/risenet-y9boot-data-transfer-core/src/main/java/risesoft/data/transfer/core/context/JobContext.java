package risesoft.data.transfer.core.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import risesoft.data.transfer.core.Engine;
import risesoft.data.transfer.core.channel.InChannel;
import risesoft.data.transfer.core.exchange.Exchange;
import risesoft.data.transfer.core.executor.ExecutorTaskQueue;
import risesoft.data.transfer.core.factory.FactoryManager;
import risesoft.data.transfer.core.handle.DoHandle;
import risesoft.data.transfer.core.handle.HandleManager;
import risesoft.data.transfer.core.job.Job;
import risesoft.data.transfer.core.job.JobEngine;
import risesoft.data.transfer.core.listener.JobListener;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.State;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 任务上下文存储任务所需的全部信息
 * 
 * @typeName JobContext
 * @date 2023年12月4日
 * @author lb
 */
public class JobContext {
	/**
	 * 统计信息
	 */
	private Communication communication;
	/**
	 * 任务id
	 */
	private String jobId;

	/**
	 * 需要执行的handle
	 */
	private HandleManager handles;
	/**
	 * 输入流
	 */
	private StreamContext streamContext;
	/**
	 * 存放着当前实例
	 */
	private Map<Class<?>, Object> instanceMap;

	/**
	 * 全局核心交换机
	 */
	private Exchange coreExchange;
	/**
	 * 任务集合
	 */
	private List<Job> jobs;

	/**
	 * 当前执行的任务
	 */
	private int concurrentJob = 0;

	/**
	 * 输入执行器
	 */
	private ExecutorTaskQueue inExecutorTaskQueue;
	/**
	 * 输入通道配置
	 */
	private Configuration inChannelConfiguration;

	/**
	 * 输出执行器
	 */
	private ExecutorTaskQueue outExecutorTaskQueue;
	/**
	 * 任务监听
	 */
	private JobListener jobListener;
	/**
	 * 是否结束
	 */
	private boolean isEnd;
	/**
	 * 日志接口
	 */
	private LoggerFactory loggerFactory;
	/**
	 * 默认日志
	 */
	private Logger logger;

	private String name;

	private Map<String, Object> contextMap;

	public JobContext(Communication communication, String jobId, HandleManager handles, JobListener jobListener) {
		super();
		this.communication = communication;
		this.jobId = jobId;
		this.handles = handles;
		this.streamContext = new StreamContext();
		this.instanceMap = new HashMap<Class<?>, Object>();
		this.contextMap = new HashMap<String, Object>();
		this.jobListener = jobListener;
		putInstance(this);
		putInstance(handles);
		putInstance(communication);
	}

	/**
	 * 获取上下文对象，这个对象可以用于缓存等操作
	 * 
	 * @return
	 */
	public Map<String, Object> getContext() {
		return contextMap;
	}

	/**
	 * 获取任务名
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取本次任务的日志工厂
	 * 
	 * @return
	 */
	public LoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	/**
	 * 获取上下文日志
	 * 
	 * @return
	 */
	public Logger getLogger() {
		return this.logger;
	}

	public void setLoggerFactory(LoggerFactory loggerFactory) {
		this.loggerFactory = loggerFactory;
		this.logger = loggerFactory.getLogger(JobContext.class);
		this.instanceMap.put(LoggerFactory.class, loggerFactory);
		this.instanceMap.put(Logger.class, logger);
	}

	public synchronized boolean isEnd() {
		return isEnd;
	}

	public synchronized void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

	public JobListener getJobListener() {
		return jobListener;
	}

	public void setJobListener(JobListener jobListener) {
		this.jobListener = jobListener;
	}

	public JobContext setCoreExchange(Exchange exchange) {
		this.coreExchange = exchange;
		return this;
	}

	public Exchange getCoreExchange() {
		return coreExchange;
	}

	public void putInstance(Class<?> key, Object instance) {
		this.instanceMap.put(key, instance);
	}

	public Map<Class<?>, Object> getInstanceMap() {
		return this.instanceMap;
	}

	/**
	 * 已实例的类追加实例
	 * 
	 * @param instance
	 */
	public void putInstance(Object instance) {
		this.instanceMap.put(instance.getClass(), instance);
	}

	/**
	 * 获取实例
	 * 
	 * @param key 需要的类型
	 * @return
	 */
	public <T> T getInstance(Class<T> key) {
		return key.cast(this.instanceMap.get(key));
	}

	public StreamContext getStreamContext() {
		return streamContext;
	}

	public Communication getCommunication() {
		return communication;
	}

	public void setCommunication(Communication communication) {
		this.communication = communication;
	}

	public String getJobId() {
		return jobId;
	}

	public HandleManager getHandles() {
		return handles;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	/**
	 * 获取下一个任务
	 * 
	 * @return
	 */
	public Job nextJob() {
		return jobs.get(this.concurrentJob++);
	}

	/**
	 * 是否存在任务
	 * 
	 * @return
	 */
	public boolean hasJob() {
		return jobs.size() > this.concurrentJob;
	}

	/**
	 * 获取当前任务
	 * 
	 * @return
	 */
	public Job getConcurrentJob() {
		return this.jobs.get(this.concurrentJob);
	}

	public ExecutorTaskQueue getInExecutorTaskQueue() {
		return inExecutorTaskQueue;
	}

	public JobContext setInExecutorTaskQueue(ExecutorTaskQueue inExecutorTaskQueue) {
		this.inExecutorTaskQueue = inExecutorTaskQueue;
		return this;
	}

	public ExecutorTaskQueue getOutExecutorTaskQueue() {
		return outExecutorTaskQueue;
	}

	public JobContext setOutExecutorTaskQueue(ExecutorTaskQueue outExecutorTaskQueue) {
		this.outExecutorTaskQueue = outExecutorTaskQueue;
		return this;
	}

	public <T> void doHandle(Class<T> handleType, DoHandle<T> doHandle) {
		this.handles.doHandle(handleType, doHandle);

	}

	public JobContext setInChannelConfiguration(Configuration inChannelConfiguration) {
		this.inChannelConfiguration = inChannelConfiguration;
		return this;
	}

	/**
	 * 停止任务
	 * 
	 * @param e 传递停止的异常信息
	 * @return
	 */
	public boolean stop(Throwable e) {
		if (this.communication.isFinished()) {
			return false;
		}
		this.communication.setState(State.KILLED);
		this.communication.setThrowable(e, true);
		return Engine.onJobFlush(this);

	}

	public InChannel getInChannel() {
		InChannel inChannel = FactoryManager.getInstanceOfConfiguration(inChannelConfiguration, InChannel.class,
				instanceMap);
		inChannel.setOutPutStream(coreExchange);
		return inChannel;
	}

}
