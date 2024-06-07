package net.risedata.jdbc.executor.log.impl;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import net.risedata.jdbc.executor.log.PrintExecutor;

/**
 * 使用log4j 打印的对象
 * 
 * @author libo 2021年3月26日
 */
public class DefaultPrintExecutor implements PrintExecutor {
	public static final String SHOW_ARGS = "net.risedata.jdbc.show.args";

	@Value("${" + SHOW_ARGS + ":false}")
	private boolean isShowArgs;

	public static final Log logger = LogFactory.getLog(DefaultPrintExecutor.class);

	@Override
	public void print(String msg, Object[] args) {

		logger.info(msg);
		if (isShowArgs) {
			for (int i = 1; i < args.length; i++) {
				if (args[i].getClass().isArray()) {
					logger.info(Arrays.toString((Object[]) args[i]));
				} else {
					logger.info(args[i]);
				}
			}
		}
	}

	/**
	 * @return the isShowArgs
	 */
	public boolean isShowArgs() {
		return isShowArgs;
	}

	/**
	 * @param isShowArgs the isShowArgs to set
	 */
	public void setShowArgs(boolean isShowArgs) {
		this.isShowArgs = isShowArgs;
	}

}
