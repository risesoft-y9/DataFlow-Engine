package risesoft.data.transfer.core.channel;

import java.util.List;

import risesoft.data.transfer.core.handle.HandleContext;
import risesoft.data.transfer.core.handle.HandleManager;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.record.RecordInHandle;
import risesoft.data.transfer.core.stream.out.RecordOutuptStream;
import risesoft.data.transfer.core.util.CloseUtils;

/**
 * 触发handle事件的输入通道
 * 
 * @typeName HandleInChannel
 * @date 2023年12月8日
 * @author lb
 */
public class HandleInChannel implements InChannel {

	private InChannel inChannel;

	private HandleContext<RecordInHandle> recordHandleContext;

	public HandleInChannel(InChannel inChannel, HandleManager handleManager) {
		this.inChannel = inChannel;
		this.recordHandleContext = handleManager.getContext(RecordInHandle.class);
	}

	@Override
	public void setOutPutStream(RecordOutuptStream recordOutuptStream) {
		inChannel.setOutPutStream(recordOutuptStream);

	}

	@Override
	public void writer(Record record) {
		recordHandleContext.handle((handle) -> {
			handle.doIn(record);
		});
		inChannel.writer(record);

	}

	@Override
	public void close() throws Exception {
		CloseUtils.close(inChannel);
	}

	@Override
	public void writer(List<Record> record) {
		recordHandleContext.handle((handle) -> {
			handle.doIn(record);
		});
		inChannel.writer(record);
	}

	@Override
	public void flush() {
		inChannel.flush();
	}

	@Override
	public void collectDirtyRecord(Record record, Throwable t, String errorMessage) {
		inChannel.collectDirtyRecord(record, t, errorMessage);

	}

}
