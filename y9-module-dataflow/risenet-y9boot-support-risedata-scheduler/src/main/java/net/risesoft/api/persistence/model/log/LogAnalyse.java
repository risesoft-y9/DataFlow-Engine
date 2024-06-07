package net.risesoft.api.persistence.model.log;

/**
 * @Description : 日志分析结束对了
 * @ClassName LogAnalyse
 * @Author lb
 * @Date 2023/11/23 9:50
 * @Version 1.0
 */
public class LogAnalyse {
	/**
	 * 任务id
	 */
	private Integer jobId;
	/**
	 * 任务名
	 */
	private String jobName;

	/**
	 * 类型
	 */
	private String type;
	/**
	 * 消息
	 */
	private String msg;
	/**
	 * 解决方案
	 */
	private String solution;
	private int endStatus;

	public LogAnalyse(Integer jobId, String jobName, String type, String msg, String solution,int endStatus) {
		this.jobId = jobId;
		this.jobName = jobName;
		this.type = type;
		this.msg = msg;
		this.solution = solution;
		this.endStatus = endStatus;
	}
	
	
	

	public int getEndStatus() {
		return endStatus;
	}




	public void setEndStatus(int endStatus) {
		this.endStatus = endStatus;
	}




	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}
}
