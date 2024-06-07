package risesoft.data.transfer.core.executor.in;

import risesoft.data.transfer.core.channel.InChannel;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.executor.Executor;
import risesoft.data.transfer.core.stream.in.DataInputStream;
import risesoft.data.transfer.core.util.CloseUtils;

/**
 * 输入执行器链接inputStream 进行数据的读取
 * 
 * @typeName InChannelExecutor
 * @date 2023年12月8日
 * @author lb
 */
public class InChannelExecutor implements Executor {

	private DataInputStream dataInputStream;

	private InChannel inChannel;

	public InChannelExecutor(DataInputStream dataInputStream, InChannel inChannel) {
		super();
		this.dataInputStream = dataInputStream;
		this.inChannel = inChannel;
	}

	@Override
	public void run(Object data) {
		dataInputStream.read((Data) data, inChannel);
	}

	@Override
	public void close() throws Exception {
		CloseUtils.close(dataInputStream);
		CloseUtils.close(inChannel);
	}

}
