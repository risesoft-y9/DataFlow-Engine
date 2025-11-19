package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DataSourceTypeDTO {

	private String id;

	private String name;
	
	private String driver;
	
	private String imgData;
	
	private Integer type;

}