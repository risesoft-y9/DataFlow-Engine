package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DataInterfaceParamsDTO {

	private String id;

	private String paramName;
	
	private String paramType;
	
	private String paramValue;
	
	private String remark;
	
	private String reqType;
	
	private Integer dataType;
	
	private String parentId;
	
	private String tenantId;
	
	private String userId;
	
	private String userName;

}