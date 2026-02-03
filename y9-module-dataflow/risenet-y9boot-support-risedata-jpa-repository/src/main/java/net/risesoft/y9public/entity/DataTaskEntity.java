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
@Table(name = "Y9_DATASERVICE_TASK")
@org.hibernate.annotations.Table(comment = "任务主体信息表", appliesTo = "Y9_DATASERVICE_TASK")
@NoArgsConstructor
@Data
public class DataTaskEntity extends BaseEntity {

	private static final long serialVersionUID = -4637563465588220054L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "NAME", length = 500, nullable = false)
	@Comment(value = "任务名称")
	private String name;
	
	@Column(name = "DESCRIPTION", length = 1200)
	@Comment(value = "描述")
	private String description;
	
	@Column(name = "BUSINESSID", length = 50)
	@Comment(value = "分类id")
	private String businessId;
	
	@Column(name = "USERID", length = 50)
	@Comment(value = "创建者id")
	private String userId;
	
	@Column(name = "USERNAME", length = 50)
	@Comment(value = "创建者")
	private String userName;
	
	@Column(name = "TENANTID", length = 50)
	@Comment(value = "租户ID")
	private String tenantId;
	
	@Column(name = "TASKTYPE", nullable = false)
	@Comment(value = "任务类型：1-单节点任务，2-同步任务")
	@ColumnDefault("2")
	private Integer taskType;
	
	@Column(name = "SYSTEMNAME", length = 50)
	@Comment(value = "任务来源")
	private String systemName;
	
	@Column(name = "EXTERNALID", length = 50)
	@Comment(value = "关联ID")
	private String externalId;

}