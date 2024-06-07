package net.risesoft.service;
/**
 * 任务服务
 * @typeName JobService
 * @date 2024年1月18日
 * @author lb
 */
public interface JobService {
/**
 * 执行任务返回任务的id
 * @param jobContext
 * @return
 */
	String executorJob(String jobContext);
}
