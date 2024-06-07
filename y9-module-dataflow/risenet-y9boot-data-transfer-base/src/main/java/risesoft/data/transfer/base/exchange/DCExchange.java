package risesoft.data.transfer.base.exchange;

import java.util.ArrayList;
import java.util.List;

import risesoft.data.transfer.core.exchange.OutChannelExchange;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 直流交换机不做任何过多处理的交换机 根据情况使用 当为本地文件传输时可以使用直流交换机
 * 
 * @typeName DCExchange
 * @date 2023年12月11日
 * @author lb
 */
public class DCExchange extends OutChannelExchange {
	/**
	 * 日志
	 */
	private Logger logger;

	/**
	 * 是否关闭
	 */
	private volatile boolean isShutdown = false;

	public DCExchange(Configuration configuration, LoggerFactory loggerFactory) {
		logger = loggerFactory.getLogger(configuration.getString("name", "DCExchange"));
	}

	@Override
	public void writer(Record record) {
		if (isShutdown) {
			return;
		}
		if (this.logger.isDebug()) {
			this.logger.debug(this, "writer: " + record);
		}
		this.channel.writer(record);
	}

	@Override
	public void writer(List<Record> record) {
		if (isShutdown) {
			return;
		}
		if (this.logger.isDebug()) {
			this.logger.debug(this, "writer: " + record.size());
		}
		this.channel.writer(new ArrayList<Record>(record));
	}

	@Override
	public void shutdown() {
		this.isShutdown = true;
		logger.info(this, "shutdown");
	}

	@Override
	public void flush() {
		if (isShutdown) {
			return;
		}
		if (logger.isDebug()) {
			logger.debug(this, "flush ");
		}
		this.channel.flush();
	}

}
