package risesoft.data.transfer.core.stream;

import risesoft.data.transfer.core.close.Closed;

/**
 * 创建数据流工厂
 * 
 * @typeName DataStreamFactory
 * @date 2023年12月11日
 * @author lb
 */
public interface DataStreamFactory<T> extends Closed {
	/**
	 * 初始化输入流 如需配置文件则从构造函数中获得配置
	 * 
	 * @param config
	 */
	void init();

	/**
	 * 获取流
	 * 
	 * @return
	 */
	T getStream();

}
