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
@Table(name = "Y9_DATASERVICE_TASKCORE")
@org.hibernate.annotations.Table(comment = "任务core块配置信息表", appliesTo = "Y9_DATASERVICE_TASKCORE")
@NoArgsConstructor
@Data
public class DataTaskCoreEntity extends BaseEntity {

	private static final long serialVersionUID = -2836485541136657512L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;
	
	@Column(name = "TASKID", length = 50, nullable = false)
	@Comment(value = "任务id")
	private String taskId;
	
	@Column(name = "ARGSID", length = 50)
	@Comment(value = "参数表id")
	private String argsId;

	@Column(name = "KEYNAME", length = 200)
	@Comment(value = "参数名称")
	private String keyName;
	
	@Column(name = "VALUE", length = 1200)
	@Comment(value = "参数值")
	private String value;
	
	@Column(name = "DATATYPE")
	@Comment(value = "种类，如：日志-printLog、input-输入、output-输出..")
	private String dataType;
	
	@Column(name = "TYPENAME", length = 50, nullable = false)
	@Comment(value = "类别：执行器-executor，交换机-exchange，输入/输出通道-channel，其它配置-plugs")
	private String typeName;
	
	@Column(name = "SEQUENCE")
	@Comment(value = "序号")
	private Integer sequence;

}