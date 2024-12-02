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
@Table(name = "Y9_DATASERVICE_INTERFACEPARAMS")
@org.hibernate.annotations.Table(comment = "接口参数信息表", appliesTo = "Y9_DATASERVICE_INTERFACEPARAMS")
@NoArgsConstructor
@Data
public class DataInterfaceParamsEntity extends BaseEntity {

	private static final long serialVersionUID = 970180555260501008L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "PARAMNAME", length = 100, nullable = false)
	@Comment(value = "参数名称")
	private String paramName;
	
	@Column(name = "PARAMTYPE", length = 20)
	@Comment(value = "参数类型")
	private String paramType;
	
	@Column(name = "PARAMVALUE", length = 500)
	@Comment(value = "参数值")
	private String paramValue;
	
	@Column(name = "REMARK", length = 500)
	@Comment(value = "说明")
	private String remark;
	
	@Column(name = "REQTYPE", length = 20)
	@Comment(value = "请求参数类型：params/headers/body")
	private String reqType;
	
	@Column(name = "DATATYPE", nullable = false)
	@Comment(value = "类型：1-返回参数， 0-请求参数")
	@ColumnDefault("0")
	private Integer dataType;
	
	@Column(name = "PARENTID", length = 20, nullable = false)
	@Comment(value = "父节点ID")
	private String parentId;
	
	@Column(name = "TENANTID", length = 50)
	@Comment(value = "租户ID")
	private String tenantId;
	
	@Column(name = "USERID", length = 50)
	@Comment(value = "创建者ID")
	private String userId;
	
	@Column(name = "USERNAME", length = 50)
	@Comment(value = "创建者")
	private String userName;

}