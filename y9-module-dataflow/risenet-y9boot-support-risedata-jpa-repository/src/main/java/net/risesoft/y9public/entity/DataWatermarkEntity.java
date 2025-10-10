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
@Table(name = "Y9_DATASERVICE_WATERMARK")
@org.hibernate.annotations.Table(comment = "数据水印信息表", appliesTo = "Y9_DATASERVICE_WATERMARK")
@NoArgsConstructor
@Data
public class DataWatermarkEntity extends BaseEntity {

	private static final long serialVersionUID = 4469325345994117969L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;
	
	@Column(name = "CONTENT", length = 1200)
	@Comment(value = "水印信息")
	private String content;
	
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