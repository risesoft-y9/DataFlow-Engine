package net.risesoft.api.persistence.model.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.risedata.jdbc.annotations.join.Join;
import net.risedata.jdbc.annotations.operation.Operate;
import net.risedata.jdbc.annotations.order.Asc;
import net.risedata.jdbc.annotations.order.Desc;
import net.risedata.jdbc.operation.Operates;

import org.hibernate.annotations.Comment;
import org.springframework.core.annotation.Order;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description : 调度日志
 * @ClassName JobLog
 * @Author lb
 * @Date 2022/8/31 9:43
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@Table(name = "Y9_DATASERVICE_JOB_LOG")
public class JobLog {
	/**
	 * 异常
	 */
	public static final int ERROR = 2;
	/**
	 * 成功
	 */
	public static final int SUCCESS = 1;
	/**
	 * 启动
	 */
	public static final int START = 0;
	/**
	 * 等待
	 */
	public static final int AWAIT = -1;
	/**
	 * 唯一id
	 */
	@Id
	@Comment(value = "id")
	@Column(name = "ID", length = 100)
	private String id;
	/**
	 * 调度状态 0 正在调度 1调度成功 2调度失败
	 */
	@Comment(value = "调度状态 0 正在调度 1调度成功 2调度失败")
	@Column(name = "STATUS", length = 1)
	private Integer status;
	/**
	 * 任务id
	 */
	@Comment(value = "任务id")
	@Column(name = "JOB_ID", length = 100)
	private Integer jobId;
	/**
	 * 执行结果
	 */
	@Comment(value = "执行结果")
	@Column(name = "RESULT", length = 4000)
	private String result;
	/**
	 * 调度服务
	 */
	@Comment(value = "调度服务")
	@Column(name = "DISPATCH_SERVER", length = 200)
	private String dispatchServer;

	/**
	 * 被调度的服务 多个使用,间隔
	 */
	@Comment(value = "被调度的服务")
	@Column(name = "DISPATCH_SOURCE", length = 2000)
	private String dispatchSource;
	/**
	 * 调度时间
	 */
	@Operate(value = Operates.GTANDEQ)
	@Desc
	@Comment(value = "调度时间戳")
	@Column(name = "DISPATCH_TIME", length = 40)
	private Long dispatchTime;
	/**
	 * 调度完成时间
	 */
	@Operate(value = Operates.LTANDGT)
	@Comment(value = "调度结束时间戳")
	@Column(name = "END_TIME", length = 200)
	private Long endTime;
	/**
	 * 调度日志
	 */
	@Lob
	@Comment(value = "调度日志")
	@Column(name = "LOG_CONSOLE", length = 4000)
	private String logConsole = "";
	/**
	 * 环境
	 */
	@Comment(value = "环境")
	@Operate(Operates.EQ)
	@Column(name = "ENVIRONMENT", length = 100)
	private String environment;
	/**
	 * 子任务id
	 */
	@Comment(value = "子任务id")
	@Column(name = "CHILD_JOB_ID", length = 100)
	private String childJobId;

	@Transient
//    @Join(joinId = { "jobId" }, toId = { "id"}, value = Job.class, field = "name")
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
