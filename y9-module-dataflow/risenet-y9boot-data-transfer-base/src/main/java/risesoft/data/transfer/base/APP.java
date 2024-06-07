package risesoft.data.transfer.base;

import java.io.File;

import risesoft.data.transfer.core.Engine;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.listener.JobListener;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.statistics.State;
import risesoft.data.transfer.core.util.Configuration;

public class APP {
	public static void main(String[] args) {

		Engine.start("aa", Configuration.from(new File("F:\\tmp\\testJob.txt")), new JobListener() {
			@Override
			public void end(Communication communication) {
				if (communication.getState() == State.SUCCEEDED) {
					System.out.println("获取成功" + communication.getLongCounter(CommunicationTool.READ_SUCCEED_BYTES));
					System.out.println("任务耗时:"+CommunicationTool.getStatistics(communication));
				} else {
					System.out.println("失败" + communication.getThrowableMessage());
				}
			}

		},null);
	}
}
