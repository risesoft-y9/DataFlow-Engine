package risesoft.data.transfer.core.action;

import risesoft.data.transfer.core.context.JobContext;

/**
 * 动作
 * @typeName Action
 * @date 2023年12月4日
 * @author lb
 */
public interface Action {
   
	void doAction(JobContext jobContext);

}
