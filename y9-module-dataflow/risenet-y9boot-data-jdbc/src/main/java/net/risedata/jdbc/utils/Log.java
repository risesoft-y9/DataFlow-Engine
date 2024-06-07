package net.risedata.jdbc.utils;
/**
 * 打印的日志类
 * @author libo
 *2021年2月18日
 */

import net.risedata.jdbc.executor.log.PrintExecutor;

public class Log {
     private static PrintExecutor defaultPrintExecutor;
     
     public static void print(String msg) {
    	 defaultPrintExecutor.print(msg,new Object[] {msg});
     }

	/**
	 * @return the defaultPrintExecutor
	 */
	public static PrintExecutor getDefaultPrintExecutor() {
		return defaultPrintExecutor;
	}

	/**
	 * @param defaultPrintExecutor the defaultPrintExecutor to set
	 */
	public static void setDefaultPrintExecutor(PrintExecutor defaultPrintExecutor) {
		Log.defaultPrintExecutor = defaultPrintExecutor;
	}
     
}
