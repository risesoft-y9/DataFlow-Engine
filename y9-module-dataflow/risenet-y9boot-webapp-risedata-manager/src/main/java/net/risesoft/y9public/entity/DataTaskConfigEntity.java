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
@Table(name = "Y9_DATASERVICE_TASKCONFIG")
@org.hibernate.annotations.Table(comment = "任务job块配置信息表", appliesTo = "Y9_DATASERVICE_TASKCONFIG")
@NoArgsConstructor
@Data
public class DataTaskConfigEntity extends BaseEntity {

	private static final long serialVersionUID = -3354597858733315657L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;
	
	@Column(name = "TASKID", length = 50, nullable = false)
	@Comment(value = "任务id")
	private String taskId;
	
	/* 源头表配置 */
	@Column(name = "SOURCENAME", length = 200, nullable = false)
	@Comment(value = "源头-执行类名称")
	private String sourceName;
	
	@Column(name = "SOURCEID", length = 50, nullable = false)
	@Comment(value = "源头-数据源id")
	private String sourceId;
	
	@Column(name = "SOURCETYPE", length = 50)
	@Comment(value = "源头-数据源类型")
	private String sourceType;
	
	@Column(name = "SOURCETABLEID", length = 50, nullable = false)
	@Comment(value = "源头-数据表")
	private String sourceTable;
	
	@Lob
	@Column(name = "SOURCECLOUMN")
	@Comment(value = "源头-字段列")
	private String sourceCloumn;
	
	@Column(name = "FETCHSIZE")
	@Comment(value = "jdbc查询时ResultSet的每次读取记录数，优化查询性能，减少网络传输和内存占用")
	@ColumnDefault("0")
	private Integer fetchSize;
	
	@Column(name = "WHERESQL", length = 2000)
	@Comment(value = "where条件语句")
	private String whereSql;
	
	@Column(name = "SPLITPK", length = 50)
	@Comment(value = "切分字段")
	private String splitPk;
	
	@Column(name = "PRECISE")
	@Comment(value = "是否精准切分，相当于groupby")
	@ColumnDefault("0")
	private Boolean precise = false;
	
	@Column(name = "TABLENUMBER")
	@Comment(value = "切分为多少块，当没设置精准切分时生效")
	private Integer tableNumber;
	
	@Column(name = "SPLITFACTOR")
	@Comment(value = "外部切分因素，当tableNumber没设值时生效")
	private Integer splitFactor;
	
	/* 目标表配置 */
	@Column(name = "TARGENAME", length = 200, nullable = false)
	@Comment(value = "目标-执行类名称")
	private String targeName;
	
	@Column(name = "TARGETID", length = 50, nullable = false)
	@Comment(value = "目标-数据源id")
	private String targetId;
	
	@Column(name = "TARGETTYPE", length = 50)
	@Comment(value = "目标-数据源类型")
	private String targetType;
	
	@Column(name = "TARGETTABLEID", length = 50, nullable = false)
	@Comment(value = "目标-数据表")
	private String targetTable;
	
	@Column(name = "WRITERTYPE", length = 20, nullable = false)
	@Comment(value = "目标-输出类型：insert、update")
	private String writerType;
	
	@Column(name = "UPDATEFIELD", length = 500)
	@Comment(value = "输出类型为update时所选字段")
	private String updateField;
	
	@Lob
	@Column(name = "TARGETCLOUMN")
	@Comment(value = "目标-字段列")
	private String targetCloumn;

}