package risesoft.data.transfer.core.exchange;

import risesoft.data.transfer.core.channel.OutChannel;

/**
 * 
 * @typeName OutChannelEdExchange
 * @date 2023年12月11日
 * @author lb
 */
public abstract class OutChannelExchange implements Exchange {

	protected OutChannel channel;

	/**
	 * 是否关闭
	 */
	protected volatile boolean isShutdown = false;

	@Override
	public void close() throws Exception {
		channel.close();
	}

	@Override
	public void setOutChannel(OutChannel channel) {
		this.channel = channel;

	}

	@Override
	public OutChannel getOutChannel() {
		return this.channel;
	}

	@Override
	public void shutdown() {
		isShutdown = true;
	}

}
