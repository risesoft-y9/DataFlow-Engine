package risesoft.data.transfer.core.log;

import java.util.Date;

import com.alibaba.fastjson2.util.DateUtils;


/**
 * 本地打印输出的日志记录器工厂
 * 
 * 
 * @typeName LocalPrintLoggerFactory
 * @date 2024年11月15日
 * @author lb
 */
public class LocalPrintLoggerFactory implements LoggerFactory {

	Logger logger;

	public LocalPrintLoggerFactory(int logLevel) {
		boolean info = logLevel <= Logger.INFO;
		boolean error = logLevel <= Logger.ERROR;
		boolean debug = logLevel <= Logger.DEBUG;

		logger = new Logger() {

			@Override
			public boolean isInfo() {
				return info;
			}

			@Override
			public boolean isError() {
				return error;
			}

			@Override
			public boolean isDebug() {
				return debug;
			}

			@Override
			public void info(Object source, String msg) {
				if (isInfo()) {
					System.out.println(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")+" info: "+source + ": " + msg);
				}
			}

			@Override
			public void error(Object source, String msg) {
				if (isError()) {
					System.out.println(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")+" error: "+source + ": " + msg);
				}
			}

			@Override
			public void debug(Object source, String msg) {
				if (isDebug()) {
					System.out.println(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")+" debug: "+source + ": " + msg);
				}
			}
		};
	}

	@Override
	public Logger getLogger(String name) {
		return logger;
	}

	@Override
	public Logger getLogger(Class<?> type) {
		return logger;
	}

}
