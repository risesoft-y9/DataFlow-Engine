package risesoft.data.transfer.core.exchange;

import risesoft.data.transfer.core.channel.OutChannel;
import risesoft.data.transfer.core.stream.out.RecordOutuptStream;

/**
 * 交换机用于数据输入管理
 * 
 * @typeName Exchange
 * @date 2023年12月4日
 * @author lb
 */
public interface Exchange extends RecordOutuptStream {
	/**
	 * 设置这个交换机的输出通道
	 * 
	 * @param channel
	 */
	void setOutChannel(OutChannel channel);

	/**
	 * 获取输出通道
	 * 
	 * @return
	 */
	OutChannel getOutChannel();

	/**
	 * 关闭
	 */
	void shutdown();
}
