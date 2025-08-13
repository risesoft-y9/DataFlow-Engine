package net.risesoft.y9public.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.risesoft.base.BaseEntity;

@Entity
@Table(name = "Y9_DATASERVICE_ERRORLOG")
@org.hibernate.annotations.Table(comment = "任务脏数据记录信息表", appliesTo = "Y9_DATASERVICE_ERRORLOG")
@NoArgsConstructor
@Data
public class DataLogEntity extends BaseEntity {

	private static final long serialVersionUID = 3547142462523521585L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "JOBID", length = 50, nullable = false)
	@Comment(value = "本次任务执行的id")
	private String jobId;

	@Lob
	@Column(name = "RECORD")
	@Comment(value = "数据内容")
	private String record;

	@Column(name = "DATE")
	@Comment(value = "任务时间")
	private long date;

	@Column(name = "MSG", length = 1000)
	@Comment(value = "异常信息")
	private String msg;

}