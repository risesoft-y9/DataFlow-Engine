package risesoft.data.transfer.core.listener;

import risesoft.data.transfer.core.statistics.Communication;

/**
 * 监听任务执行情况
 * 
 * @typeName JobListener
 * @date 2023年12月4日
 * @author lb
 */
public interface JobListener {

	void end(Communication communication);
}
