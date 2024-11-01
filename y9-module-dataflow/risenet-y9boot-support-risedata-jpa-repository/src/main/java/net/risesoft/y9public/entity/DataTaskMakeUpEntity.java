package net.risesoft.y9public.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.risedata.jdbc.annotations.operation.Operate;
import net.risedata.jdbc.annotations.order.Asc;
import net.risedata.jdbc.operation.Operates;
import net.risesoft.base.BaseEntity;

@Entity
@Table(name = "Y9_DATASERVICE_TASKMAKEUP")
@org.hibernate.annotations.Table(comment = "任务配置组成数据表", appliesTo = "Y9_DATASERVICE_TASKMAKEUP")
@NoArgsConstructor
@Data
public class DataTaskMakeUpEntity extends BaseEntity {

	private static final long serialVersionUID = 5696157153508557026L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "TYPENAME", length = 100, nullable = false)
	@Comment(value = "类别名称")
	private String typeName;

	@Column(name = "NAMEVALUE", length = 800, nullable = false)
	@Comment(value = "name值")
	private String nameValue;

	@Column(name = "ARGSVALUE", length = 2000, nullable = false)
	@Comment(value = "args参数值")
	private String argsValue;
	
	@Operate(value = Operates.EQ)
	@Column(name = "TASKID", length = 50, nullable = false)
	@Comment(value = "任务id")
	private String taskId;

	@Column(name = "TABINDEX", nullable = false)
	@Comment(value = "排序号")
	@Asc
	private Integer tabIndex;

}