package risesoft.data.transfer.core.handle;

import risesoft.data.transfer.core.util.Configuration;

/**
 * 初始化全局配置handle
 * 
 * @typeName InitApplicationConfigHandle
 * @date 2023年12月8日
 * @author lb
 */
public interface InitApplicationConfigHandle extends Handle {
	/**
	 * 初始化全局配置
	 * 
	 * @param configuration
	 */
	void initApplicationConfig(Configuration configuration);
}
