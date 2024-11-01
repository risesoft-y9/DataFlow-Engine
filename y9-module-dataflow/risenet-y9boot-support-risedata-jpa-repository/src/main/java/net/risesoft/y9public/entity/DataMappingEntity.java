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
@Table(name = "Y9_DATASERVICE_MAPPING")
@org.hibernate.annotations.Table(comment = "配置执行类映射信息表", appliesTo = "Y9_DATASERVICE_MAPPING")
@NoArgsConstructor
@Data
public class DataMappingEntity extends BaseEntity {

	private static final long serialVersionUID = 5571420816048750967L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "CLASSNAME", length = 200, nullable = false)
	@Comment(value = "执行类名称")
	private String className;
	
	@Column(name = "DESCRIPTION", length = 800)
	@Comment(value = "描述")
	private String description;
	
	@Column(name = "FUNCTYPE", length = 100, nullable = false)
	@Comment(value = "功能类型")
	private String funcType;
	
	@Column(name = "TYPENAME", length = 100, nullable = false)
	@Comment(value = "类别名称")
	private String typeName;
	
	@Column(name = "ONLYONE", nullable = false)
	@Comment(value = "唯一性：1-唯一 ， 0-不唯一")
	@ColumnDefault("1")
	private Integer onlyOne;

}