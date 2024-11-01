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
@Table(name = "Y9_DATASERVICE_DATATABLEFIELD")
@org.hibernate.annotations.Table(comment = "数据库表字段信息表", appliesTo = "Y9_DATASERVICE_DATATABLEFIELD")
@NoArgsConstructor
@Data
public class DataTableField extends BaseEntity {

	private static final long serialVersionUID = 6379152722342548646L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "NAME")
	@Comment(value = "名称")
	private String name;
	
	@Column(name = "CNAME")
	@Comment(value = "中文名称")
	private String cname;
	
	@Column(name = "TABLEID")
	@Comment(value = "表ID")
	private String tableId;
	
	@Column(name = "TYPENUM")
	@Comment(value = "字段类型参数")
	private Integer typeNum;
	
	@Column(name = "FIELDTYPE")
	@Comment(value = "字段类型")
	private String fieldType;
	
	@Column(name = "FIELDLENGTH")
	@Comment(value = "字段长度")
	private String fieldLength;
	
	@Column(name = "FIELDNULL")
	@Comment(value = "是否允许为空 , YES是,NO否")
	private String fieldNull;
	
	@Column(name = "FIELDPK")
	@Comment(value = "是否主键,N否,Y是")
	private String fieldPk;
	
	@Column(name = "DISPLAYORDER")
	@Comment(value = "显示排序")
	private Integer displayOrder;
	
	@Column(name = "ISSTATE")
	@Comment(value = "字段是否在数据库中生成")
	@ColumnDefault("0")
	private Boolean isState;
	
	@Column(name = "UPDATEVERSION")
	@Comment(value = "修改版本")
	private Integer updateVersion;
	
	@Column(name = "OLDNAME")
	@Comment(value = "旧名称")
	private String oldName;

}