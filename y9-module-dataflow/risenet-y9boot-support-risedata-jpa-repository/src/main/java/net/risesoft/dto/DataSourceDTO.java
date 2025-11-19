package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DataSourceDTO {

	private String id;

	private String baseName;
	
	private String baseType;
	
	private String driver;

	private String url;

	private String username;

	private String password;

	private Integer initialSize;

	private Integer maxActive;

	private Integer minIdle;
	
	private String baseSchema;
	
	private String directory;
	
	private Integer runType;
	
	private String remark;
	
	private Integer isLook;
	
	private Integer type;
	
	private String tenantId;
	
	private String userId;

}