package net.risesoft.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class RequestModel {
	
	private String method;
	
	private String url;
	
	private List<Map<String, Object>> headers;
	
	private List<Map<String, Object>> params;
	
	private String body;
	
	private String contentType;
}
