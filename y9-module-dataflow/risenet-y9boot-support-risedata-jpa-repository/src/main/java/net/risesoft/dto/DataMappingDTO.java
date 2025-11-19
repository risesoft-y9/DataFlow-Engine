package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DataMappingDTO {

	private String id;

	private String className;
	
	private String description;
	
	private String funcType;
	
	private String typeName;
	
	private Integer onlyOne;

}