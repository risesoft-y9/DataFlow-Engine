package risesoft.data.transfer.core.listener.impl;

import risesoft.data.transfer.core.listener.JobListener;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.statistics.State;

/**
 * 获取任务返回值的监听器
 * 
 * @typeName ResultJobListener
 * @date 2024年1月26日
 * @author lb
 */
public class ResultJobListener implements JobListener {

	/**
	 * 返回的统计对象
	 */
	private Communication communication;

	private Object LOCK = new Object();

	@Override
	public void end(Communication communication) {
		synchronized (LOCK) {
			this.communication = communication;
			LOCK.notify();
		}
	}

	/**
	 * 是否结束
	 * 
	 * @return
	 */
	public boolean isEnd() {
		return this.communication != null;
	}

	/**
	 * 是否成功
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return getCommunication().getState() == State.SUCCEEDED;
	}

	/**
	 * 获取统计对象,注意此时没有统计 对象时会等待
	 * 
	 * @return
	 */
	public Communication getCommunication() {
		if (communication == null) {
			synchronized (LOCK) {
				if (communication == null) {
					try {
						LOCK.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return communication;
	}

	/**
	 * 获取任务信息
	 * 
	 * @return
	 */
	public String getMessage() {
		Communication communication = getCommunication();
		if (communication.getState() == State.SUCCEEDED) {
			return CommunicationTool.getStatistics(communication);
		} else {
			return communication.getThrowableMessage();
		}
	}

}
