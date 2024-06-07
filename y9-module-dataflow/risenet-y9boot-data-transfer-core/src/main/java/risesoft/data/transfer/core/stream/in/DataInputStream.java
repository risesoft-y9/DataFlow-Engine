package risesoft.data.transfer.core.stream.in;

import risesoft.data.transfer.core.channel.InChannel;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.stream.DataStream;

/**
 * 数据输入流
 * 
 * @typeName DataInputStream
 * @date 2023年12月4日
 * @author lb
 */
public interface DataInputStream extends DataStream {

	/**
	 * 读取数据
	 * 
	 * @param data
	 */
	void read(Data data,InChannel inChannel);

}
