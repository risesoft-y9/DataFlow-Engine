package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务编排信息
 * @author pzx
 *
 */
@NoArgsConstructor
@Data
public class DataArrangeDTO {

	private String id;

	private String name;
	
	private String content;
	
	private String xmlData;
	
	private Integer pattern;
	
	private String userId;
	
	private String userName;
	
	private String tenantId;
	
	private String cron;

}