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
@Table(name = "Y9_DATASERVICE_BUSINESS")
@org.hibernate.annotations.Table(comment = "业务分类信息表", appliesTo = "Y9_DATASERVICE_BUSINESS")
@NoArgsConstructor
@Data
public class DataBusinessEntity extends BaseEntity {

	private static final long serialVersionUID = 7481153360867061076L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "NAME", length = 100, nullable = false)
	@Comment(value = "名称")
	private String name;
	
	@Column(name = "PARENTID", length = 38)
	@Comment(value = "父节点ID")
	private String parentId;

}