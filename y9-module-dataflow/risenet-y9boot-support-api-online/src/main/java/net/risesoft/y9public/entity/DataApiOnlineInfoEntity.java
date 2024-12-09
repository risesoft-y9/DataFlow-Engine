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
@Table(name = "Y9_DATASERVICE_API_ONLINE_INFO")
@org.hibernate.annotations.Table(comment = "接口详细信息表", appliesTo = "Y9_DATASERVICE_API_ONLINE_INFO")
@NoArgsConstructor
@Data
public class DataApiOnlineInfoEntity extends BaseEntity {

	private static final long serialVersionUID = -8854294421940798800L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Lob
	@Column(name = "FORMDATA")
	@Comment(value = "内容")
	private String formData;

}