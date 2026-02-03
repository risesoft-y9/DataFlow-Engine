package net.risesoft.y9public.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.risesoft.base.BaseEntity;
import net.risesoft.converter.EncryptConverter;

@Entity
@Table(name = "Y9_DATASERVICE_DATASOURCE")
@org.hibernate.annotations.Table(comment = "数据源信息表", appliesTo = "Y9_DATASERVICE_DATASOURCE")
@NoArgsConstructor
@Data
public class DataSourceEntity extends BaseEntity {

	private static final long serialVersionUID = 3314110133933384423L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "BASENAME", length = 100, nullable = false)
	@Comment(value = "数据源名称")
	private String baseName;
	
	@Column(name = "BASETYPE", length = 100, nullable = false)
	@Comment(value = "数据源类别")
	private String baseType;
	
	@Column(name = "DRIVER", length = 100)
	@Comment(value = "驱动")
	private String driver;

	@Column(name = "URL", length = 400, nullable = false)
	@Comment(value = "连接URL")
	private String url;

	@Column(name = "USERNAME", length = 50)
	@Comment(value = "用户名")
	private String username;

	@Column(name = "PASSWORD", length = 200)
	@Comment(value = "密码")
	@Convert(converter = EncryptConverter.class)
	private String password;

	@Column(name = "INITIALSIZE")
	@Comment(value = "数据库初始化大小")
	private Integer initialSize;

	@Column(name = "MAXACTIVE")
	@Comment(value = "参数maxActive")
	private Integer maxActive;

	@Column(name = "MINIDLE")
	@Comment(value = "参数minIdle")
	private Integer minIdle;
	
	@Column(name = "BASESCHEMA", length = 50)
	@Comment(value = "模式")
	private String baseSchema;
	
	@Column(name = "DIRECTORY", length = 400)
	@Comment(value = "文件目录")
	private String directory;
	
	@Column(name = "RUNTYPE")
	@Comment(value = "ftp运行模式，1-被动模式，0-主动模式")
	private Integer runType;
	
	@Column(name = "REMARK", length = 500)
	@Comment(value = "备注")
	private String remark;
	
	@Column(name = "ISLOOK", nullable = false)
	@Comment(value = "是否允许查看元数据信息，1-可以，0-不可以")
	@ColumnDefault("0")
	private Integer isLook;
	
	@Column(name = "TYPE", nullable = false)
	@Comment(value = "类别，1-其他数据源，0-数据库")
	@ColumnDefault("0")
	private Integer type;
	
	@Column(name = "TENANTID", length = 50)
	@Comment(value = "租户ID")
	private String tenantId;
	
	@Column(name = "USERID", length = 50)
	@Comment(value = "创建者ID")
	private String userId;
	
	@Column(name = "SYSTEMNAME", length = 50)
	@Comment(value = "任务来源")
	private String systemName;
	
	@Column(name = "EXTERNALID", length = 50)
	@Comment(value = "关联ID")
	private String externalId;

}