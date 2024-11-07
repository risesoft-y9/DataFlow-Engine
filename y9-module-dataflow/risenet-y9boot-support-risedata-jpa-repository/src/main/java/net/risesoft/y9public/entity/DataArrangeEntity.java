package net.risesoft.y9public.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.risesoft.base.BaseEntity;

@Entity
@Table(name = "Y9_DATASERVICE_ARRANGE")
@org.hibernate.annotations.Table(comment = "任务编排信息表", appliesTo = "Y9_DATASERVICE_ARRANGE")
@NoArgsConstructor
@Data
public class DataArrangeEntity extends BaseEntity {

	private static final long serialVersionUID = -62125486228679758L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "NAME", length = 600, nullable = false)
	@Comment(value = "名称")
	private String name;
	
	@Column(name = "CONTENT", length = 1200)
	@Comment(value = "编排描述")
	private String content;
	
	@Lob
	@Column(name = "XMLDATA")
	@Comment(value = "流程内容")
	private String xmlData;
	
	@Column(name = "PATTERN", nullable = false)
	@Comment(value = "状态：1-禁用，0-正常")
	@ColumnDefault("0")
	private Integer pattern;
	
	@Column(name = "USERID", length = 50)
	@Comment(value = "创建者ID")
	private String userId;
	
	@Column(name = "USERNAME", length = 50)
	@Comment(value = "创建者")
	private String userName;
	
	@Column(name = "TENANTID", length = 50)
	@Comment(value = "租户ID")
	private String tenantId;

}