package risesoft.data.transfer.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import risesoft.data.transfer.core.channel.JoinOutExecutorChannel;
import risesoft.data.transfer.core.channel.OutChannel;
import risesoft.data.transfer.core.config.ConfigLoadManager;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.context.StreamContext;
import risesoft.data.transfer.core.exchange.CoreExchange;
import risesoft.data.transfer.core.exchange.Exchange;
import risesoft.data.transfer.core.executor.ExecutorTaskQueue;
import risesoft.data.transfer.core.factory.FactoryManager;
import risesoft.data.transfer.core.handle.InitApplicationConfigHandle;
import risesoft.data.transfer.core.job.Job;
import risesoft.data.transfer.core.job.JobEndHandle;
import risesoft.data.transfer.core.job.JobEngine;
import risesoft.data.transfer.core.listener.JobListener;
import risesoft.data.transfer.core.listener.impl.ResultJobListener;
import risesoft.data.transfer.core.log.HandledLoggerFactory;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.plug.PlugManager;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.State;
import risesoft.data.transfer.core.stream.in.DataInputStreamFactory;
import risesoft.data.transfer.core.stream.out.DataOutputStreamFactory;
import risesoft.data.transfer.core.util.CloseUtils;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ConfigurationConst;
import risesoft.data.transfer.core.util.ValueUtils;

/**
 * 组织 启动 根据任务配置加载对应的组件类 多个任务共享核心组件。 { "job":[{ "input":{ "name":"${输出器名字}"
 * "args":{ ... } }, "output":{ "name":"${输出器名字}" "args":{ ... } } }], "core":{
 * "channel":{ "out":{ "name":"${通道class}", "args":{ ... } }, "in":{
 * "name":"${输入交换机class}" "args":{ ... } } }, "exchange":{
 * "name":"${核心交换机class}" "args":{ ... }
 * 
 * }, "executor":{ "input":{ "name":"${输入器执行线程池}" "args":{ ... } }, "output":{
 * "name":"${输出器执行线程池}" "args":{ ... } }, }, "errorLimit":{ "record":${脏数据条数},
 * "percentage":${比例} } }, "plugs":[ { "name":"${class名字}" "args":{ ... } } ] }
 * 
 * @typeName Engine
 * @date 2023年12月4日
 * @author lb
 */
public class Engine {

	private static final String JOB_NAME_KEY = "core.name";

	/**
	 * 启动任务 返回任务结束参数获取对象
	 * 
	 * @param jobId         任务id
	 * @param configuration 配置文件
	 * @return
	 */
	public static ResultJobListener start(String jobId, Configuration configuration) {
		ResultJobListener resultJobListener = new ResultJobListener();
		resultJobListener.setJobContext(start(jobId, configuration, resultJobListener, null));
		return resultJobListener;
	}

	/**
	 * 启动任务
	 * 
	 * @param jobId         任务id
	 * @param configuration 配置文件
	 * @param jobListener   任务监听器
	 * @return
	 */
	public static JobContext start(String jobId, Configuration configuration, JobListener jobListener) {
		return start(jobId, configuration, jobListener, null);
	}

	/**
	 * 启动任务
	 * 
	 * @param jobId         任务id
	 * @param configuration 配置
	 * @param jobListener   任务监听器
	 * @param loggerFactory 日志工厂
	 * @return
	 */
	public static JobContext start(String jobId, Configuration configuration, JobListener jobListener,
			LoggerFactory loggerFactory) {
		JobContext jobContext = new JobContext(new Communication(), jobId, jobListener,configuration);
		try {
			// 输出通道连接输出器
			if (loggerFactory == null) {
				loggerFactory = new HandledLoggerFactory(jobContext.getHandles());
				jobContext.getHandles().add((HandledLoggerFactory) loggerFactory,jobContext);
			}
			jobContext.setName(configuration.getString(JOB_NAME_KEY, Thread.currentThread().getName()));
			jobContext.setLoggerFactory(loggerFactory);
			PlugManager.loadRootPlug(configuration, jobContext);
			// 替换配置文件读取公式
			Configuration loadedConfiguration = ConfigLoadManager.loadConfig(configuration, jobContext);
			jobContext.putInstance(loadedConfiguration);
			//加载插件
			PlugManager.loadPlug(loadedConfiguration, jobContext);
			//调用初始化上下文事件
			jobContext.doHandle(InitApplicationConfigHandle.class, (handle) -> {
				handle.initApplicationConfig(loadedConfiguration);
			});

			jobContext.getLogger().info(Engine.class, "正在装配核心组件");
			createJobs(loadedConfiguration, jobContext);
			jobContext
					.setInExecutorTaskQueue(FactoryManager.getInstanceOfConfiguration(ValueUtils.getRequired(
							loadedConfiguration.getConfiguration(ConfigurationConst.EXECUTOR_INPUT), "缺少输入队列执行器"),
							ExecutorTaskQueue.class, jobContext.getInstanceMap()))
					.setInChannelConfiguration(ValueUtils
							.getRequired(loadedConfiguration.getConfiguration(ConfigurationConst.IN_CHANNEL), "缺失输入通道"))
					.setOutExecutorTaskQueue(
							FactoryManager
									.getInstanceOfConfiguration(
											ValueUtils.getRequired(loadedConfiguration
													.getConfiguration(ConfigurationConst.EXECUTOR_OUTPUT), "缺少输出队列执行器"),
											ExecutorTaskQueue.class, jobContext.getInstanceMap()))
					.setCoreExchange(
							new CoreExchange(
									FactoryManager.getInstanceOfConfiguration(
											ValueUtils.getRequired(loadedConfiguration
													.getConfiguration(ConfigurationConst.CORE_EXCHANGE), "缺少核心交换机"),
											Exchange.class, jobContext.getInstanceMap()),
									jobContext.getCommunication()))
					.getCoreExchange()
					// 使用一个链接的
					.setOutChannel(FactoryManager.getInstanceOfConfiguration(
							ValueUtils.getRequired(loadedConfiguration.getConfiguration(ConfigurationConst.OUT_CHANNEL),
									"缺失输出通道"),
							OutChannel.class, jobContext.getInstanceMap()));
			jobContext.getCoreExchange().getOutChannel()
					.setOutPutStream(new JoinOutExecutorChannel(jobContext.getOutExecutorTaskQueue()));
			jobContext.getLogger().info(Engine.class, "组件装配完成任务开始");
			return JobEngine.start(jobContext);
		} catch (Throwable e) {
			jobContext.getCommunication().setThrowable(e, true);
			jobContext.getCommunication().setState(State.FAILED);
			onJobFlush(jobContext);
			jobContext.getLogger().error(Engine.class, "初始化任务失败" + e.getMessage());
		}
		return jobContext;
	}

	/**
	 * 创建任务
	 * 
	 * @param configuration
	 * @param jobContext
	 */
	private static void createJobs(Configuration configuration, JobContext jobContext) {
		List<Configuration> jobConfigs = ValueUtils
				.getRequired(configuration.getListConfiguration(ConfigurationConst.JOBS), "没有任务");
		List<Job> jobs = new ArrayList<Job>();
		for (Configuration jobConfig : jobConfigs) {
			Job job = new Job();
			job.setStreamContext(new StreamContext());
			job.getStreamContext()
					.setDataInputStreamFactory(FactoryManager.getInstanceOfConfiguration(
							ValueUtils.getRequired(jobConfig.getConfiguration(ConfigurationConst.INPUT), "未找到输入流"),
							DataInputStreamFactory.class, jobContext.getInstanceMap()));
			job.getStreamContext()
					.setDataOutputStreamFactory(FactoryManager.getInstanceOfConfiguration(
							ValueUtils.getRequired(jobConfig.getConfiguration(ConfigurationConst.OUTPUT), "未找到输出流"),
							DataOutputStreamFactory.class, jobContext.getInstanceMap()));
			jobs.add(job);
		}
		jobContext.setJobs(jobs);
	}

	/**
	 * 判断任务是否结束并输出对应内容
	 * 
	 * @param jobContext
	 */
	public static boolean onJobFlush(JobContext jobContext) {
		if (jobContext.isEnd()) {
			return true;
		}

		Communication communication = jobContext.getCommunication();
		if (communication.isFinished()) {
			shutdown(jobContext);
			jobContext.setEnd(true);
			try {
				jobContext.doHandle(JobEndHandle.class, (h) -> {
					h.onJobEnd(jobContext);
				});
			} catch (Exception e) {
				communication.setState(State.FAILED, true);
				communication.setThrowable(e);
			}
			jobContext.getJobListener().end(communication);
			return true;
		}
		return false;

	}

	/**
	 * 停止任务
	 * 
	 * @param jobContext
	 */
	private static void shutdown(JobContext jobContext) {

		CloseUtils.close(jobContext.getOutExecutorTaskQueue());
		try {
			jobContext.getOutExecutorTaskQueue().shutdown();
		} catch (Exception e) {
			try {
				jobContext.getLogger().error(jobContext, "close OutExecutorTaskQueue error："+ExceptionUtils.getStackTrace(e));
			} catch (Exception e2) {
			}
		}
		try {
			jobContext.getInExecutorTaskQueue().shutdown();
		} catch (Exception e) {
			try {
				jobContext.getLogger().error(jobContext, "close InExecutorTaskQueue error："+ExceptionUtils.getStackTrace(e));
			} catch (Exception e2) {
			}
		}
		try {
			jobContext.getCoreExchange().shutdown();
		} catch (Exception e) {
			try {
				jobContext.getLogger().error(jobContext, "close CoreExchange error："+ExceptionUtils.getStackTrace(e));
			} catch (Exception e2) {
			}
		}
	}
}
