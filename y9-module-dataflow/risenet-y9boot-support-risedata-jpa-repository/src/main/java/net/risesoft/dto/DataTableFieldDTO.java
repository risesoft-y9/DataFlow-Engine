package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DataTableFieldDTO {

	private String id;

	private String name;
	
	private String cname;
	
	private String tableId;
	
	private Integer typeNum;
	
	private String fieldType;
	
	private String fieldLength;
	
	private String fieldNull;
	
	private String fieldPk;
	
	private Integer displayOrder;
	
	private Boolean isState;
	
	private Integer updateVersion;
	
	private String oldName;

}