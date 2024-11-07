package net.risesoft.y9public.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.risesoft.base.BaseEntity;

@Entity
@Table(name = "Y9_DATASERVICE_ARRANGE_LOG")
@org.hibernate.annotations.Table(comment = "编排日志信息表", appliesTo = "Y9_DATASERVICE_ARRANGE_LOG")
@NoArgsConstructor
@Data
public class DataArrangeLogEntity extends BaseEntity {

	private static final long serialVersionUID = 6510283053016794606L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "ARRANGEID", length = 50, nullable = false)
	@Comment(value = "任务编排ID")
	private String arrangeId;
	
	@Column(name = "PROCESSID", length = 50, nullable = false)
	@Comment(value = "流程ID")
	private String processId;
	
	@Column(name = "JOBID")
	@Comment(value = "任务ID")
	private Integer jobId;
	
	@Column(name = "JOBNAME", length = 300)
	@Comment(value = "任务名称")
	private String jobName;
	
	@Column(name = "JOBLOGID", length = 50)
	@Comment(value = "任务日志ID")
	private String jobLogId;
	
	@Column(name = "ERRORMSG", length = 500)
	@Comment(value = "任务执行前的校验不通过和报错信息")
	private String errorMsg;
	
	@Column(name = "IDENTIFIER")
	@Comment(value = "执行标识符")
	private Integer identifier;

}