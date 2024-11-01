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
@Table(name = "Y9_DATASERVICE_DATATABLE")
@org.hibernate.annotations.Table(comment = "数据库表信息表", appliesTo = "Y9_DATASERVICE_DATATABLE")
@NoArgsConstructor
@Data
public class DataTable extends BaseEntity {

	private static final long serialVersionUID = -261209776540614483L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "NAME")
	@Comment(value = "表名称")
	private String name;
	
	@Column(name = "CNAME")
	@Comment(value = "表中文名称")
	private String cname;
	
	@Column(name = "BASEID")
	@Comment(value = "数据源ID")
	private String baseId;
	
	@Column(name = "TABLECOUNT")
	@Comment(value = "表字段数量")
	private Integer tableCount;
	
	@Column(name = "STATUS")
	@Comment(value = "数据表状态， 0:未生成物理表    1：生成物理表")
	@ColumnDefault("0")
	private Integer status;
	
	@Column(name = "SERVICETABLE")
	@Comment(value = "是否业务表 0：不是   1：是")
	@ColumnDefault("0")
	private Integer serviceTable;
	
	@Column(name = "OLDNAME")
	@Comment(value = "旧名称")
	private String oldName;

}