package net.risesoft.api.persistence.model.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.risedata.jdbc.annotations.join.Join;
import net.risedata.jdbc.annotations.order.Desc;
import net.risesoft.api.persistence.model.GetService;
import net.risesoft.security.GetEnvironment;
import net.risesoft.y9public.entity.DataBusinessEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;

import java.util.Date;

/**
 * @Description :
 * @ClassName Job
 * @Author lb
 * @Date 2022/8/29 18:11
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@Table(name = "Y9_DATASERVICE_JOB")
@org.hibernate.annotations.Table(comment = "调度任务表", appliesTo = "Y9_DATASERVICE_JOB")
public class Job implements GetEnvironment, GetService {

	/**
	 * id
	 */
	@Id
	@Comment(value = "id")
	@Column(name = "ID", length = 100)
	private Integer id;

	/**
	 * 服务id
	 */
	@Comment(value = "所属服务的ID")
	@NotBlank(message = "服务id不能为空")
	@Column(name = "SERVICE_ID", length = 100)
	private String serviceId;

	/**
	 * 定时调度类型分为http 和job job 走rpc http 走http请求
	 */
	@Comment(value = "任务调度类型")
	@NotBlank(message = "类型名字不能为空")
	@Column(name = "JOB_TYPE", length = 100)
	private String type;

	/**
	 * 当 type 为http 的时候使用的是http 调度url 为地址 其他情况下这个是任务名字
	 */
	@Comment(value = "调度的任务源")
	@NotBlank(message = "source不能为空")
	@Column(name = "SOURCE", length = 100)
	private String source;

	/**
	 * speed 当调度类型为cron 则为cron 如果是固定速度则为固定速度 固定速度单位秒
	 */
	@Comment(value = "速度")
	@NotBlank(message = "速度类型不能为空")
	@Column(name = "SPEED", length = 100)
	private String speed;

	/**
	 * 调度类型 cron 或者固定速度
	 */
	@Comment(value = "调度速度类型")
	@NotBlank(message = "速度表达式不能为空")
	@Column(name = "DISPATCH_TYPE", length = 100)
	private String dispatchType;

	/**
	 * 阻塞策略 串行: 添加到等待执行队列依次执行 当前一个执行后去调用任务队列里面的数据 当执行器执行的时候则去判断是否存在任务 丢弃后续调度
	 * 的时候就会丢弃后续调度 并行 继续调度
	 */
	@Comment(value = "阻塞策略")
	@NotBlank(message = "阻塞策略不能为空")
	@Column(name = "BLOCKING_STRATEGY", length = 100)
	private String blockingStrategy;

	/**
	 * 调度方式 分片 ,广播 ,轮询,故障转移,永远第一个
	 */
	@Comment(value = "调度方式")
	@NotBlank(message = "调度方式不能为空")
	@Column(name = "DISPATCH_METHOD", length = 100)
	private String dispatchMethod;

	/**
	 * 调度参数 对分片广播的时候生效默认为index 可以使用数组参数
	 */
	@Column(name = "DISPATCH_ARGS", length = 500)
	private String dispatchArgs;

	/**
	 * 管理员信息
	 */
	@Comment(value = "管理员信息")
	@Column(name = "MANAGER", length = 100)
	private String manager;

	/**
	 * 邮箱多个使用,分离
	 */
	@Comment(value = "邮箱信息")
	@Column(name = "EMAIL", length = 100)
	private String email;

	/**
	 * 调度参数：任务配置id，多个用逗号分割
	 */
	@Comment(value = "调度")
	@Column(name = "ARGS", length = 2000)
	private String args;

	/**
	 * 子任务id 多个用 ,号
	 */
	@Comment(value = "子任务id,多个并行子任务")
	@Column(name = "CHILD_JOBS", length = 100)
	private String childJobs;

	/**
	 * 调度任务整个超时时间单位秒
	 */
	@Comment(value = "调度总超时间")
	@NotNull(message = "超时时间不能为空")
	@Column(name = "TIME_OUT", length = 10)
	private Integer timeOut;

	/**
	 * 任务超时时间单位秒
	 */
	@Comment(value = "调度超时间")
	@NotNull(message = "超时时间不能为空")
	@Column(name = "SOURCE_TIME_OUT", length = 10)
	private Integer sourceTimeOut;

	/**
	 * 失败重试次数
	 */
	@Comment(value = "重试次数")
	@NotNull(message = "重试次数不能为空")
	@Column(name = "ERROR_COUNT", length = 2)
	private Integer errorCount;

	/**
	 * 最近更新时间
	 */
	@Comment(value = "最近更新时间")
	@Column(name = "UPDATE_TIME", length = 40)
	private Long updateTime;

	/**
	 * 创建时间
	 */
	@Comment(value = "创建时间")
	@Desc
	@Column(name = "CREATE_DATE", length = 40)
	private Date createDate;

	/**
	 * 任务所属环境
	 */
	@Comment(value = "任务所属环境")
	@NotBlank(message = "环境不能为空")
	@Column(name = "ENVIRONMENT", length = 30)
	private String environment;

	/**
	 * 是否启动0代表不启动1代表启动
	 */
	@Comment(value = "任务状态0代表不启动1代表启动")
	@NotNull(message = "状态不能为空")
	@Column(name = "STATUS", length = 2)
	private Integer status;

	/**
	 * 调度服务 由哪一台服务器调度和监控一个原理
	 */
	@Comment(value = "由哪台管控服务调度")
	@Column(name = "DISPATCH_SERVER", length = 100)
	private String dispatchServer;

	/**
	 * 描述
	 */
	@Comment(value = "任务描述")
	@Column(name = "DESCRIPTION", length = 200)
	private String description;

	/**
	 * 任务类型 用户标记字段
	 */
	@Comment(value = "任务类型")
	@NotBlank(message = "任务类型不能为空")
	@Column(name = "SERVICE_JOB_TYPE", length = 200)
	private String jobType;

	@Transient
	@Join(value = DataBusinessEntity.class, joinId = "jobType", toId = "id", field = "name")
	private String jobTypeName;

	/**
	 * 名字
	 */
	@Comment(value = "任务名")
	@Column(name = "JOB_NAME", length = 200)
	private String name;

	/**
	 * 任务来源
	 */
	@Comment(value = "任务来源")
	@Column(name = "JOB_SOURCE", length = 200)
	private String jobSource;

	public String getJobTypeName() {
		return jobTypeName;
	}

	public void setJobTypeName(String jobTypeName) {
		this.jobTypeName = jobTypeName;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJobSource() {
		return jobSource;
	}

	public void setJobSource(String jobSource) {
		this.jobSource = jobSource;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public Integer getSourceTimeOut() {
		return sourceTimeOut;
	}

	public void setSourceTimeOut(Integer sourceTimeOut) {
		this.sourceTimeOut = sourceTimeOut;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getDispatchType() {
		return dispatchType;
	}

	public void setDispatchType(String dispatchType) {
		this.dispatchType = dispatchType;
	}

	public String getBlockingStrategy() {
		return blockingStrategy;
	}

	public void setBlockingStrategy(String blockingStrategy) {
		this.blockingStrategy = blockingStrategy;
	}

	public String getDispatchMethod() {
		return dispatchMethod;
	}

	public void setDispatchMethod(String dispatchMethod) {
		this.dispatchMethod = dispatchMethod;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public String getChildJobs() {
		return childJobs;
	}

	public void setChildJobs(String childJobs) {
		this.childJobs = childJobs;
	}

	public Integer getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDispatchArgs() {
		return dispatchArgs;
	}

	public void setDispatchArgs(String dispatchArgs) {
		this.dispatchArgs = dispatchArgs;
	}

	public String getDispatchServer() {
		return dispatchServer;
	}

	public void setDispatchServer(String dispatchServer) {
		this.dispatchServer = dispatchServer;
	}

	@Override
	public String getEnvironment() {
		return environment;
	}

	@Override
	public String toString() {
		return "Job{" + "id=" + id + ", serviceId='" + serviceId + '\'' +

				", type='" + type + '\'' + ", source='" + source + '\'' + ", speed='" + speed + '\''
				+ ", dispatchType='" + dispatchType + '\'' + ", blockingStrategy='" + blockingStrategy + '\''
				+ ", dispatchMethod='" + dispatchMethod + '\'' + ", manager='" + manager + '\'' + ", email='" + email
				+ '\'' + ", args='" + args + '\'' + ", childJobs='" + childJobs + '\'' + ", timeOut=" + timeOut
				+ ", errorCount=" + errorCount + ", updateTime=" + updateTime + ", createDate=" + createDate
				+ ", environment='" + environment + '\'' + ", status=" + status + ", dispatchServer='" + dispatchServer
				+ '\'' + ", description='" + description + '\'' + '}';
	}

	@Override
	public String getService() {
		return serviceId;
	}
}
