package net.risesoft.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class JobDTO {

	private Integer id;

	private String serviceId;

	private String type;

	private String source;

	private String speed;

	private String dispatchType;

	private String blockingStrategy;

	private String dispatchMethod;

	private String dispatchArgs;

	private String manager;

	private String email;

	private String args;

	private String childJobs;

	private Integer timeOut;

	private Integer sourceTimeOut;

	private Integer errorCount;

	private Long updateTime;

	private Date createDate;

	private String environment;

	private Integer status;

	private String dispatchServer;

	private String description;

	private String jobType;

	private String jobTypeName;

	private String name;

	private String jobSource;
	
}
