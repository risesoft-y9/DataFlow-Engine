package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.pojo.Y9Result;

public interface HomeDataService {

	/**
	 * 当前运行的任务情况
	 * @param type  Public-默认环境，dev-测试环境
	 * @return
	 */
	Y9Result<Map<String, Object>> getCurrentTaskInfo(String type);

	/**
	 * 获取近期每日调度次数
	 * @param type  Public-默认环境，dev-测试环境
	 * @return
	 */
	Y9Result<Map<String, Object>> getDailySchedulingFrequencyInfo(String type);

	/**
	 * 获取正常任务状态比例
	 * @param type
	 * @return
	 */
	List<Map<String, Object>> getTaskStateInfo(String type);

	/**
	 * 获取调度情况
	 * @param type
	 * @return
	 */
	Map<String, Object> getSchedulingInfo(String type);

}
