package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.pojo.Y9Result;
import net.risesoft.pojo.home.HomeData;
import net.risesoft.pojo.home.HomeData.CurrentTaskInfo;
import net.risesoft.pojo.home.HomeData.DailySchedulingFrequencyInfo;
import net.risesoft.pojo.home.HomeData.JobLogInfo;
import net.risesoft.pojo.home.HomeData.SchedulingInfo;
import net.risesoft.pojo.home.HomeQueryModel.CurrentTaskQueryInfo;
import net.risesoft.pojo.home.HomeQueryModel.DailySchedulingFrequencyQueryInfo;
import net.risesoft.pojo.home.HomeQueryModel.JobLogQueryInfo;
import net.risesoft.pojo.home.HomeQueryModel.SchedulingQueryInfo;
import net.risesoft.pojo.home.HomeQueryModel.TaskStateQueryInfo;

public interface HomeDataService {

	/**
	 * 获取首页全部数据
	 *
	 * @MethodName: getHomeData
	 * @Description: TODO
	 * @author
	 * @return Y9Result<HomeData>
	 *
	 */
	Y9Result<HomeData> getHomeData();

	Y9Result<HomeData> getHomeDataSync();

	/**
	 * 获取当前任务执行情况
	 *
	 * @MethodName: getCurrentTaskInfo
	 * @Description: TODO
	 * @author
	 * @param currentTaskQueryInfo
	 * @return CurrentTaskInfo
	 *
	 */
	CurrentTaskInfo getCurrentTaskInfo(CurrentTaskQueryInfo currentTaskQueryInfo);

	/**
	 *
	 * @MethodName: getDailySchedulingFrequencyInfo
	 * @Description: TODO 获取每日调度频率
	 * @author
	 * @param dailySchedulingFrequencyQueryInfo
	 * @return DailySchedulingFrequencyInfo
	 *
	 */
	DailySchedulingFrequencyInfo getDailySchedulingFrequencyInfo(
			DailySchedulingFrequencyQueryInfo dailySchedulingFrequencyQueryInfo);

	/**
	 * 获取正常任务状态比例
	 *
	 * @MethodName: getTaskStateInfo
	 * @Description: TODO
	 * @author
	 * @param taskStateQueryInfo
	 * @return TaskStateInfo
	 *
	 */
	List<Map<String, Object>> getTaskStateInfo(TaskStateQueryInfo taskStateQueryInfo);

	/**
	 * 获取调度情况
	 *
	 * @MethodName: getTaskStateInfo
	 * @Description: TODO
	 * @author
	 * @param schedulingQueryInfo
	 * @return TaskStateInfo
	 *
	 */
	SchedulingInfo getSchedulingInfo(SchedulingQueryInfo schedulingQueryInfo);

	/**
	 * 获取日志信息成功失败
	 *
	 * @MethodName: getJobLogInfo
	 * @Description: TODO
	 * @author
	 * @param jobLogQueryInfo
	 * @return List<JobLogInfo>
	 *
	 */
	JobLogInfo getJobLogInfo(JobLogQueryInfo jobLogQueryInfo);

}
