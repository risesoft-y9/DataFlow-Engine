package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务分类信息
 * @author pzx
 *
 */
@NoArgsConstructor
@Data
public class DataBusinessDTO {

	private String id;

	private String name;
	
	private String parentId;

}