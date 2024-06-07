package net.risesoft.api.job.creator;


import net.risesoft.api.job.JobContext;

/**
 * 创建名字的方法
 * 
 * @author lb
 *
 */
public interface CreatorMethod {
	/**
	 * 参数集
	 * 
	 * @param args
	 * @return
	 */
	String create(JobContext context, String[] args);
}
