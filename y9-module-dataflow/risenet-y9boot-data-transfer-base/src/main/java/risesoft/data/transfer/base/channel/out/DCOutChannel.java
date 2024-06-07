package risesoft.data.transfer.base.channel.out;

import java.util.List;
import risesoft.data.transfer.core.channel.OutputStreamOutChannel;
import risesoft.data.transfer.core.record.Record;

/**
 * 直流通道直接将输出流向输出的地方 不做过多处理
 * 
 * @typeName BufferOutChannel
 * @date 2023年12月11日
 * @author lb
 */
public class DCOutChannel extends OutputStreamOutChannel{

	@Override
	public void writer(Record record) {
		recordOutuptStream.writer(record);
	}

	@Override
	public void writer(List<Record> record) {
		recordOutuptStream.writer(record);
	}

	@Override
	public void flush() {
	}

}
