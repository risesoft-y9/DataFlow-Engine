package risesoft.data.transfer.core.log;

/**
 * 日志类产生工厂
 * 
 * @typeName LoggerFactory
 * @date 2023年12月25日
 * @author lb
 */
public interface LoggerFactory {

	/**
	 * 获取日志类
	 * 
	 * @param type
	 * @return
	 */
	Logger getLogger(String name);

	/**
	 * 类型
	 * 
	 * @param type
	 * @return
	 */
	Logger getLogger(Class<?> type);
}
