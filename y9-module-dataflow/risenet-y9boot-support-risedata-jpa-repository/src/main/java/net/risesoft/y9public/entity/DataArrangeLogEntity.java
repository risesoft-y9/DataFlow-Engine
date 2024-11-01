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
	
	@Lob
	@Column(name = "LOGDATA")
	@Comment(value = "内容日志")
	private String logData;

}