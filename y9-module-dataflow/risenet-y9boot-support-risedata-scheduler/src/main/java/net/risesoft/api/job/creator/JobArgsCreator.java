package net.risesoft.api.job.creator;


import net.risesoft.api.job.JobContext;

public interface JobArgsCreator {
	/**
	 * 参数解析
	 * 
	 * @param context
	 * @param creatorStr
	 * @return
	 */
	String creator(JobContext context, String creatorStr);
}
