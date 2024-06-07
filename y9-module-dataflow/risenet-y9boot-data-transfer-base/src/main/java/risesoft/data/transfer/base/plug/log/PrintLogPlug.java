package risesoft.data.transfer.base.plug.log;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.handle.HandleManager;
import risesoft.data.transfer.core.log.LogDebugHandle;
import risesoft.data.transfer.core.log.LogErrorHandle;
import risesoft.data.transfer.core.log.LogInfoHandle;
import risesoft.data.transfer.core.plug.Plug;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 直接打印日志
 * 
 * @typeName PrintLogPlug
 * @date 2023年12月26日
 * @author lb
 */
public class PrintLogPlug implements Plug {

	private static final int DEBUG = 0;
	private static final int INFO = 1;

	private static final int ERROR = 2;

	public PrintLogPlug(Configuration configuration, HandleManager handleManager) {

		int level = configuration.getInt("level", 0);
		if (level <= DEBUG) {
			handleManager.add(new LogDebugHandle() {
				@Override
				public void debug(Object source, String debug) {
					System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss ")  + debug);
				}
			});
		}
		if (level <= INFO) {

			handleManager.add(new LogInfoHandle() {

				@Override
				public void info(Object source, String info) {
					System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss ") + info);

				}
			});
		}
		if (level <= ERROR) {

			handleManager.add(new LogErrorHandle() {

				@Override
				public void error(Object source, String msg) {
					System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss ")  + msg);

				}
			});
		}
	}

	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

}
