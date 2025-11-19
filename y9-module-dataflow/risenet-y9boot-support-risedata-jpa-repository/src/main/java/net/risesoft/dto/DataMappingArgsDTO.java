package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DataMappingArgsDTO {

	private String id;
	
	private String mappingId;
	
	private String upName;

	private String name;
	
	private String description;
	
	private String defaultValue;
	
	private String type;
	
	private Integer dataType;

}