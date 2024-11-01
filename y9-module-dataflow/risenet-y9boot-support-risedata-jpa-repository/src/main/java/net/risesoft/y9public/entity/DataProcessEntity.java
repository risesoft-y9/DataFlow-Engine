package net.risesoft.y9public.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.risesoft.base.BaseEntity;

@Entity
@Table(name = "Y9_DATASERVICE_PROCESS")
@org.hibernate.annotations.Table(comment = "节点流程信息表", appliesTo = "Y9_DATASERVICE_PROCESS")
@NoArgsConstructor
@Data
public class DataProcessEntity extends BaseEntity {

	private static final long serialVersionUID = 5321083921118473218L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "ARRANGEID", length = 50, nullable = false)
	@Comment(value = "任务编排ID")
	private String arrangeId;
	
	@Column(name = "STEP", nullable = false)
	@Comment(value = "步骤序号")
	private Integer step;
	
	@Column(name = "JOBIDS", length = 500, nullable = false)
	@Comment(value = "任务ID")
	private String jobIds;
	
	@Column(name = "SUBJOBS", length = 500)
	@Comment(value = "副任务ID，并行副网关设置的任务")
	private String subJobs;
	
	@Column(name = "GATEWAY", nullable = false)
	@Comment(value = "网关类型：0-无，1-与，2-或")
	@ColumnDefault("0")
	private Integer gateway;

}