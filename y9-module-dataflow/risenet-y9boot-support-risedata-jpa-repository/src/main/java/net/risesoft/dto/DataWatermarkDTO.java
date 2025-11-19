package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DataWatermarkDTO {

	private String id;
	
	private String content;
	
	private String userId;
	
	private String userName;
	
	private String tenantId;

}