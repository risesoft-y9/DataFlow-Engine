package risesoft.data.transfer.base.plug.log;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.job.JobStartHandle;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.plug.Plug;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 定时打印任务情况
 * 
 * @typeName TimerPrintTaskStatus
 * @date 2023年12月29日
 * @author lb
 */
public class TimerPrintTaskStatus implements Plug, JobStartHandle {

	private Communication communication;
	private int timer;
	private Logger logger;

	public TimerPrintTaskStatus(Communication communication, Configuration configuration, LoggerFactory loggerFactory) {
		this.communication = communication;
		this.timer = configuration.getInt("timer", 5) * 1000;
		this.logger = loggerFactory.getLogger(TimerPrintTaskStatus.class);
	}

	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

	@Override
	public void onJobStart(JobContext jobContext) {
		System.out.println("启动");
		new Thread(() -> {
			while (!communication.isFinished()) {
				logger.info(this, CommunicationTool.getStateInfo(communication, jobContext));
				try {
					Thread.sleep(timer);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}).start();

	}

}
