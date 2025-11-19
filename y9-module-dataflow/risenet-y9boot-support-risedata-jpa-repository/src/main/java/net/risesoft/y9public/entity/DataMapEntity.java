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
@Table(name = "Y9_DATASERVICE_DATAMAP")
@org.hibernate.annotations.Table(comment = "字段、数据映射信息表", appliesTo = "Y9_DATASERVICE_DATAMAP")
@NoArgsConstructor
@Data
public class DataMapEntity extends BaseEntity {

	private static final long serialVersionUID = 1337437836893679117L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "SOURCEID", length = 50, nullable = false)
	@Comment(value = "原数据库ID")
	private String sourceId;
	
	@Column(name = "SOURCETABLE", length = 50, nullable = false)
	@Comment(value = "原数据表名称")
	private String sourceTable;
	
	@Column(name = "ORIGINALDATA", length = 200, nullable = false)
	@Comment(value = "原始数据")
	private String originalData;
	
	@Column(name = "TARGETID", length = 50, nullable = false)
	@Comment(value = "目标数据库ID")
	private String targetId;
	
	@Column(name = "TARGETTABLE", length = 50, nullable = false)
	@Comment(value = "目标数据表名称")
	private String targetTable;
	
	@Column(name = "TARGETDATA", length = 200, nullable = false)
	@Comment(value = "目标数据")
	private String targetData;
	
	@Column(name = "DATATYPE", length = 50, nullable = false)
	@Comment(value = "数据类型：field-字段映射，data-数据字典映射")
	private String dataType;
	
	@Column(name = "FIELDNAME", length = 50)
	@Comment(value = "原数据表字段名称，当dataType为data时不能为空")
	private String fieldName;

}