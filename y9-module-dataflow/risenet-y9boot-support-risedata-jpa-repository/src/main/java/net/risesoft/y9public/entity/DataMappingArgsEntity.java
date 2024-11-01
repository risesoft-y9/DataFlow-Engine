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
@Table(name = "Y9_DATASERVICE_MAPPINGARGS")
@org.hibernate.annotations.Table(comment = "配置执行类参数信息表", appliesTo = "Y9_DATASERVICE_MAPPINGARGS")
@NoArgsConstructor
@Data
public class DataMappingArgsEntity extends BaseEntity {

	private static final long serialVersionUID = 4060108737203853671L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;
	
	@Column(name = "MAPPINGID", length = 50, nullable = false)
	@Comment(value = "映射表id")
	private String mappingId;
	
	@Column(name = "UPNAME", length = 50)
	@Comment(value = "上一级名称")
	private String upName;

	@Column(name = "NAME", length = 50, nullable = false)
	@Comment(value = "名称")
	private String name;
	
	@Column(name = "DESCRIPTION", length = 200)
	@Comment(value = "描述")
	private String description;
	
	@Column(name = "DEFAULTVALUE", length = 50)
	@Comment(value = "缺省值")
	private String defaultValue;
	
	@Column(name = "TYPE", length = 50, nullable = false)
	@Comment(value = "值类别：string/integer/boolean")
	private String type;
	
	@Column(name = "DATATYPE", nullable = false)
	@Comment(value = "参数类型：1-一级参数，2-二级参数")
	private Integer dataType;

}