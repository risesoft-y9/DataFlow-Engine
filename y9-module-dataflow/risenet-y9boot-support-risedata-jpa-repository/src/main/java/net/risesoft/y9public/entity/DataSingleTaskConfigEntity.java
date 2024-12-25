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
@Table(name = "Y9_DATASERVICE_SINGLETASK")
@org.hibernate.annotations.Table(comment = "单任务配置信息表", appliesTo = "Y9_DATASERVICE_SINGLETASK")
@NoArgsConstructor
@Data
public class DataSingleTaskConfigEntity extends BaseEntity {

	private static final long serialVersionUID = 1489264053486911958L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "任务ID")
	private String id;
	
	@Column(name = "SOURCEID", length = 50, nullable = false)
	@Comment(value = "数据源id")
	private String sourceId;
	
	@Column(name = "SOURCETYPE", length = 50, nullable = false)
	@Comment(value = "数据源类型")
	private String sourceType;
	
	@Column(name = "SOURCETABLEID", length = 50, nullable = false)
	@Comment(value = "数据表")
	private String sourceTable;
	
	@Column(name = "WHERESQL", length = 2000, nullable = false)
	@Comment(value = "where条件语句")
	private String whereSql;
	
	@Column(name = "writerType")
	@Comment(value = "操作类型：update/delete")
	private String writerType;
	
	@Lob
	@Column(name = "CONFIGDATA")
	@Comment(value = "配置")
	private String configData;
	
}