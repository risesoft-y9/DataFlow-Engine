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
@Table(name = "Y9_DATASERVICE_INTERFACE")
@org.hibernate.annotations.Table(comment = "接口信息表", appliesTo = "Y9_DATASERVICE_INTERFACE")
@NoArgsConstructor
@Data
public class DataInterfaceEntity extends BaseEntity {

	private static final long serialVersionUID = -6083221056448653818L;

	@Id
	@Column(name = "ID", length = 38, nullable = false)
	@Comment(value = "主键")
	private String id;

	@Column(name = "INTERFACENAME", length = 800, nullable = false)
	@Comment(value = "接口名称")
	private String interfaceName;
	
	@Column(name = "INTERFACEURL", length = 500, nullable = false)
	@Comment(value = "接口地址")
	private String interfaceUrl;
	
	@Column(name = "REQUESTTYPE", length = 20, nullable = false)
	@Comment(value = "请求方式")
	private String requestType;
	
	@Column(name = "DATATYPE", nullable = false)
	@Comment(value = "类型：1-外置， 0-内置")
	@ColumnDefault("1")
	private Integer dataType;

}