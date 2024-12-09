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
@Table(name = "Y9_DATASERVICE_API_ONLINE")
@org.hibernate.annotations.Table(comment = "接口在线信息表", appliesTo = "Y9_DATASERVICE_API_ONLINE")
@NoArgsConstructor
@Data
public class DataApiOnlineEntity extends BaseEntity {

	private static final long serialVersionUID = -4109523700335204141L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "NAME", length = 500, nullable = false)
	@Comment(value = "名称")
	private String name;
	
	@Column(name = "PARENTID", length = 50, nullable = false)
	@Comment(value = "父节点ID")
	private String parentId;
	
	@Column(name = "TYPE", length = 50, nullable = false)
	@Comment(value = "类别：folder/api")
	private String type;

}