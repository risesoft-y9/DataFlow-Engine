package risesoft.data.transfer.core.channel;

import risesoft.data.transfer.core.stream.out.RecordOutuptStream;

/**
 * 抽象类处理好 recordOut 的存储
 * 
 * @typeName OutuptStreamOutChannel
 * @date 2023年12月11日
 * @author lb
 */
public abstract class OutputStreamOutChannel implements OutChannel {

	protected RecordOutuptStream recordOutuptStream;

	@Override
	public void setOutPutStream(RecordOutuptStream recordOutuptStream) {
		this.recordOutuptStream = recordOutuptStream;

	}

	@Override
	public void close() throws Exception {
		recordOutuptStream.close();

	}

}
