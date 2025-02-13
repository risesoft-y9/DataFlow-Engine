package risesoft.data.transfer.core.statistics;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.FrameworkErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.util.StrUtil;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 统计信息业务层面处理
 * 
 * @typeName CommunicationTool
 * @date 2023年12月4日
 * @author lb
 */
public final class CommunicationTool {
	public static final String STAGE = "stage";
	public static final String BYTE_SPEED = "byteSpeed";
	public static final String RECORD_SPEED = "recordSpeed";
	public static final String PERCENTAGE = "percentage";

	public static final String READ_SUCCEED_RECORDS = "readSucceedRecords";
	public static final String READ_SUCCEED_BYTES = "readSucceedBytes";

	public static final String READ_FAILED_RECORDS = "readFailedRecords";
	public static final String READ_FAILED_BYTES = "readFailedBytes";

	public static final String WRITE_RECEIVED_RECORDS = "writeReceivedRecords";
	public static final String WRITE_RECEIVED_BYTES = "writeReceivedBytes";

	public static final String WRITE_FAILED_RECORDS = "writeFailedRecords";
	public static final String WRITE_FAILED_BYTES = "writeFailedBytes";

	private static final String WRITE_SUCCEED_RECORDS = "writeSucceedRecords";
	private static final String WRITE_SUCCEED_BYTES = "writeSucceedBytes";

	public static final String READ_JOB_END = "readJobEnd";
	public static final String READ_JOB_START = "readJobStart";

	public static final String WRITER_JOB_END = "writerJobEnd";
	public static final String WRITER_JOB_START = "writerJobStart";

	public static Communication getReportCommunication(Communication now, Communication old, int totalStage) {
		Validate.isTrue(now != null && old != null, "为汇报准备的新旧metric不能为null");

		long totalReadRecords = getTotalReadRecords(now);
		long totalReadBytes = getTotalReadBytes(now);
		now.setLongCounter(WRITE_SUCCEED_RECORDS, getWriteSucceedRecords(now));
		now.setLongCounter(WRITE_SUCCEED_BYTES, getWriteSucceedBytes(now));

		long timeInterval = now.getTimestamp() - old.getTimestamp();
		long sec = timeInterval <= 1000 ? 1 : timeInterval / 1000;
		long bytesSpeed = (totalReadBytes - getTotalReadBytes(old)) / sec;
		long recordsSpeed = (totalReadRecords - getTotalReadRecords(old)) / sec;

		now.setLongCounter(BYTE_SPEED, bytesSpeed < 0 ? 0 : bytesSpeed);
		now.setLongCounter(RECORD_SPEED, recordsSpeed < 0 ? 0 : recordsSpeed);
		now.setDoubleCounter(PERCENTAGE, now.getLongCounter(STAGE) / (double) totalStage);

		if (old.getThrowable() != null) {
			now.setThrowable(old.getThrowable());
		}

		return now;
	}

	public static long getTotalReadRecords(final Communication communication) {
		return communication.getLongCounter(READ_SUCCEED_RECORDS) + communication.getLongCounter(READ_FAILED_RECORDS);
	}

	public static long getTotalReadBytes(final Communication communication) {
		return communication.getLongCounter(READ_SUCCEED_BYTES) + communication.getLongCounter(READ_FAILED_BYTES);
	}

	public static String getStatistics(Communication communication) {
		long transferCosts = (communication.getEndTime() - communication.getStartTime()) / 1000;
		if (0L == transferCosts) {
			transferCosts = 1L;
		}
		long byteSpeedPerSecond = communication.getLongCounter(CommunicationTool.READ_SUCCEED_BYTES) / transferCosts;
		long recordSpeedPerSecond = communication.getLongCounter(CommunicationTool.READ_SUCCEED_RECORDS)
				/ transferCosts;
		return String.format(
				"\n" + "%-26s: %19s\n" + "%-26s: %-18s\n" + "%-26s: %-18s\n" + "%-26s: %19s\n" + "%-26s: %19s\n"
						+ "%-26s: %19s\n" + "%-26s: %19s\n" + "%-26s: %19s\n" + "%-26s: %19s\n" + "%-26s: %19s\n",
				"任务总流量", StrUtil.stringify(communication.getLongCounter(CommunicationTool.READ_SUCCEED_BYTES)),
				"任务启动时刻", DateFormatUtils.format(new Date(communication.getStartTime()), "yyyy-MM-dd HH:mm:ss"),

				"任务结束时刻", DateFormatUtils.format(new Date(communication.getEndTime()), "yyyy-MM-dd HH:mm:ss"), "任务输入情况",
				String.format("%s-%s %s/s",
						DateFormatUtils.format(new Date(communication.getLongCounter(CommunicationTool.READ_JOB_START)),
								"HH:mm:ss"),
						DateFormatUtils.format(new Date(communication.getLongCounter(CommunicationTool.READ_JOB_END)),
								"HH:mm:ss"),
						(communication.getLongCounter(CommunicationTool.READ_JOB_END)
								- communication.getLongCounter(CommunicationTool.READ_JOB_START)) / 1000),

				"任务输出情况",
				String.format("%s-%s %d/s",
						DateFormatUtils.format(
								new Date(communication.getLongCounter(CommunicationTool.WRITER_JOB_START)), "HH:mm:ss"),
						DateFormatUtils.format(new Date(communication.getLongCounter(CommunicationTool.WRITER_JOB_END)),
								"HH:mm:ss"),
						(communication.getLongCounter(CommunicationTool.WRITER_JOB_END)
								- communication.getLongCounter(CommunicationTool.WRITER_JOB_START)) / 1000),
				"任务总计耗时", transferCosts + "s", "任务平均流量", StrUtil.stringify(byteSpeedPerSecond) + "/s", "记录写入速度",
				String.valueOf(recordSpeedPerSecond) + "rec/s", "读出记录总数",
				String.valueOf(CommunicationTool.getTotalReadRecords(communication)), "读写失败总数",
				String.valueOf(CommunicationTool.getTotalErrorRecords(communication)));

	}

	public static long getTotalErrorRecords(final Communication communication) {
		return communication.getLongCounter(READ_FAILED_RECORDS) + communication.getLongCounter(WRITE_FAILED_RECORDS);
	}

	public static long getRecordSize(List<Record> records, int start, int end) {
		long size = 0L;
		for (int i = start; i < end; i++) {
			size += records.get(i).getByteSize();

		}
		return size;
	}

	public static RecordSize getRecordSizeOfSpeed(List<Record> records, int start, int end, int speed) {
		RecordSize recordSize = new RecordSize();
		if (speed == 0) {
			recordSize.record = end;
			recordSize.size = getRecordSize(records, start, end);
			return recordSize;
		}
		long size = 0L;
		for (int i = start; i < end; i++) {
			size += records.get(i).getByteSize();
			if (size == speed) {
				recordSize.size = size;
				recordSize.record = i;
				break;
			} else if (size > speed) {
				recordSize.size = size - records.get(i).getByteSize();
				recordSize.record = i - 1;
				break;
			}
			recordSize.size = size;
			recordSize.record = i;
		}
		if (recordSize.record < start) {
			throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR,
					"无法分割的限速:" + speed + ",实际大小:" + records.get(start).getByteSize());
		}
		recordSize.record++;
		return recordSize;
	}

	public static long getRecordSize(List<Record> records) {
		return getRecordSize(records, 0, records.size());
	}

	public static long getTotalErrorBytes(final Communication communication) {
		return communication.getLongCounter(READ_FAILED_BYTES) + communication.getLongCounter(WRITE_FAILED_BYTES);
	}

	public static long getWriteSucceedRecords(final Communication communication) {
		return communication.getLongCounter(WRITE_RECEIVED_RECORDS)
				- communication.getLongCounter(WRITE_FAILED_RECORDS);
	}

	public static long getWriteSucceedBytes(final Communication communication) {
		return communication.getLongCounter(WRITE_RECEIVED_BYTES) - communication.getLongCounter(WRITE_FAILED_BYTES);
	}

	public static class Stringify {
		private final static DecimalFormat df = new DecimalFormat("0.00");

		private static String getSpeed(final Communication communication) {
			return String.format("%s/s, %d records/s", StrUtil.stringify(communication.getLongCounter(BYTE_SPEED)),
					communication.getLongCounter(RECORD_SPEED));
		}

		public static String unitTime(long time) {
			return unitTime(time, TimeUnit.NANOSECONDS);
		}

		public static String unitTime(long time, TimeUnit timeUnit) {
			return String.format("%,.3fs", ((float) timeUnit.toNanos(time)) / 1000000000);
		}

		public static String unitSize(long size) {
			if (size > 1000000000) {
				return String.format("%,.2fG", (float) size / 1000000000);
			} else if (size > 1000000) {
				return String.format("%,.2fM", (float) size / 1000000);
			} else if (size > 1000) {
				return String.format("%,.2fK", (float) size / 1000);
			} else {
				return size + "B";
			}
		}

		private static String getPercentage(final Communication communication) {
			return df.format(communication.getDoubleCounter(PERCENTAGE) * 100) + "%";
		}
	}

	public static String getStatus(State state) {
		switch (state) {
		case RUNNING:

			return "运行中";
		case WAITING:

			return "休眠中";
		case FAILED:

			return "执行失败";
		case SUCCEEDED:
			return "成功了";

		default:
			return "未知";
		}
	}

	public static String getStateInfo(Communication communication, JobContext jobContext) {

		return String.format(
				"任务状态:%s       | 输入数据总量:%d/record %d/byte     | 输出数据总量:%d/record %d/byte     | 剩余输入任务:  %d       |剩余输出任务: %d       | 耗时: %d/s ",
				getStatus(communication.getState()), communication.getLongCounter(READ_SUCCEED_RECORDS),
				communication.getLongCounter(READ_SUCCEED_BYTES), communication.getLongCounter(WRITE_SUCCEED_RECORDS),
				communication.getLongCounter(WRITE_SUCCEED_BYTES), jobContext.getInExecutorTaskQueue().getResidueSize(),
				jobContext.getOutExecutorTaskQueue().getResidueSize(),
				(System.currentTimeMillis() - communication.getStartTime()) / 1000);
	}

}
