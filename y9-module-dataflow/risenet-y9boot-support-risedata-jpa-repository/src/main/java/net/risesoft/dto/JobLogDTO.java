package net.risesoft.dto;

public class JobLogDTO {
	
	private String id;
	
	private Integer status;
	
	private Integer jobId;
	
	private String result;
	
	private String dispatchServer;

	private String dispatchSource;
	
	private Long dispatchTime;
	
	private Long endTime;
	
	private String logConsole = "";
	
	private String environment;
	
	private String childJobId;

	private String jobName;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getChildJobId() {
		return childJobId;
	}

	public void setChildJobId(String childJobId) {
		this.childJobId = childJobId;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getDispatchServer() {
		return dispatchServer;
	}

	public String getDispatchSource() {
		return dispatchSource;
	}

	public void setDispatchSource(String dispatchSource) {
		this.dispatchSource = dispatchSource;
	}

	public void setDispatchServer(String dispatchServer) {
		this.dispatchServer = dispatchServer;
	}

	public Long getDispatchTime() {
		return dispatchTime;
	}

	public void setDispatchTime(Long dispatchTime) {
		this.dispatchTime = dispatchTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public String getLogConsole() {
		return logConsole;
	}

	public void setLogConsole(String logConsole) {
		this.logConsole = logConsole;
	}

	public void appendConsole(String logConsole) {
		this.logConsole += logConsole;
	}

	@Override
	public String toString() {
		return "JobLog{" + "id='" + id + '\'' + ", status=" + status + ", jobId=" + jobId + ", result='" + result + '\''
				+ ", dispatchServer='" + dispatchServer + '\'' + ", dispatchTime=" + dispatchTime + ", endTime="
				+ endTime + ", logConsole='" + logConsole + '\'' + '}';
	}
}
