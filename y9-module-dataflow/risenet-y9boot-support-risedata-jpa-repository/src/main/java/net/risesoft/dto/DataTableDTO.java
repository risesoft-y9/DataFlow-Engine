package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DataTableDTO {

	private String id;

	private String name;
	
	private String cname;
	
	private String baseId;
	
	private Integer tableCount;
	
	private Integer status;
	
	private Integer serviceTable;
	
	private String oldName;

}