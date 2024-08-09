package risesoft.data.transfer.base.plug.data;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.handle.DirtyRecordHandle;
import risesoft.data.transfer.core.job.JobEndHandle;
import risesoft.data.transfer.core.plug.Plug;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 脏数据控制插件
 * 
 * @typeName DirtyDataPlug
 * @date 2023年12月27日
 * @author lb
 */
public class DirtyDataPlug implements Plug, DirtyRecordHandle, JobEndHandle {

	private static final String DIRTY_DATA = "DIRTY_DATA";

	private Communication communication;
	private int record;
	private double percentage;
	private int size = 0;

	public DirtyDataPlug(Communication communication, Configuration configuration) {
		record = configuration.getInt("record", -1);
		percentage = configuration.getDouble("percentage", 0.0);
		this.communication = communication;
	}

	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

	@Override
	public void collectDirtyRecord(Record record, Throwable t, String errorMessage) {
		size++;
		if (this.record != -1 && size > this.record) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "脏数据超出条数限制:" + record + "错误信息:" + errorMessage, t);
		}
		if (communication.getMessage(DIRTY_DATA) == null || communication.getMessage(DIRTY_DATA).size() < 10) {
			communication.addMessage(DIRTY_DATA, "脏数据:" + record + "error:" + errorMessage + "\n");
		}

	}

	@Override
	public void onJobEnd(JobContext jobContext) {
		long failedRecords = communication.getLongCounter(CommunicationTool.WRITE_FAILED_RECORDS);
		long size = communication.getLongCounter(CommunicationTool.READ_SUCCEED_RECORDS);
		if (percentage > 0.0 && (double) failedRecords / size > percentage) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR,
					"脏数据超出比例限制!" + communication.getMessage(DIRTY_DATA));
		}

	}

}
