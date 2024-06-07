package risesoft.data.transfer.core.channel;

import risesoft.data.transfer.core.stream.out.RecordOutuptStream;

/**
 * 通道 下链接输出器上链接交换机,通道限流,交换机不限流
 * 
 * @typeName Channel
 * @date 2023年12月4日
 * @author lb
 */
public interface Channel extends RecordOutuptStream {
	/**
	 * 设置通道的出口
	 * 
	 * @param exchange
	 */
	void setOutPutStream(RecordOutuptStream recordOutuptStream);
	
	
}
