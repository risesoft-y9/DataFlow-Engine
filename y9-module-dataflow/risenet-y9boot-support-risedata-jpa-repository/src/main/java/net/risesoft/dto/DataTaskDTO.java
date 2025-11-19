package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DataTaskDTO {

	private String id;

	private String name;
	
	private String description;
	
	private String businessId;
	
	private String userId;
	
	private String userName;
	
	private String tenantId;
	
	private Integer taskType;

}