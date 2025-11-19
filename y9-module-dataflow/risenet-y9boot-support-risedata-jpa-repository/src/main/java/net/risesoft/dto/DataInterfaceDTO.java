package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DataInterfaceDTO {

	private String id;

	private String interfaceName;
	
	private String interfaceUrl;
	
	private String requestType;
	
	private String contentType;
	
	private Integer dataType;
	
	private Integer pattern;
	
	private String tenantId;
	
	private String userId;
	
	private String userName;

}