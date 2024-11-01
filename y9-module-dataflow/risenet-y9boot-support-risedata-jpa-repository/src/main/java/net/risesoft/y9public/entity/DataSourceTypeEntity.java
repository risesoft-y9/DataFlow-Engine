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
@Table(name = "Y9_DATASERVICE_DATASOURCETYPE")
@org.hibernate.annotations.Table(comment = "数据源分类信息表", appliesTo = "Y9_DATASERVICE_DATASOURCETYPE")
@NoArgsConstructor
@Data
public class DataSourceTypeEntity extends BaseEntity {

	private static final long serialVersionUID = -4838002144379237300L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "NAME", length = 100, nullable = false)
	@Comment(value = "名称")
	private String name;
	
	@Column(name = "DRIVER", nullable = false)
	@Comment(value = "驱动")
	private String driver;
	
	@Lob
	@Column(name = "IMGDATA")
	@Comment(value = "icon")
	private String imgData;
	
	@Column(name = "TYPE")
	@Comment(value = "类别，1-其他数据源，0-数据库")
	@ColumnDefault("0")
	private Integer type;

}