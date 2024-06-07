package net.risesoft.service.impl;

import net.risesoft.service.JobService;
import risesoft.data.transfer.core.Engine;
import risesoft.data.transfer.core.listener.JobListener;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.State;
import risesoft.data.transfer.core.util.Configuration;

public class JobServiceImpl implements JobService {

	@Override
	public String executorJob(String jobContext) {
		// TODO Auto-generated method stub
		Engine.start("id", Configuration.from(jobContext), new JobListener() {

			@Override
			public void end(Communication comm) {
				if (comm.getState() == State.SUCCEEDED) {
					// 返回信息
				} else {
					comm.getThrowable().printStackTrace();
					comm.getThrowableMessage();
				}

			}
		}, null);
		return null;
	}

}
