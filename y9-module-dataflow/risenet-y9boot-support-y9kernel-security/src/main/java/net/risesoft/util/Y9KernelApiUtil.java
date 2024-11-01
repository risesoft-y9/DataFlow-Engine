package net.risesoft.util;

import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.json.Y9JsonUtil;

public class Y9KernelApiUtil {
	
	static String serverUrl = Y9Context.getProperty("y9.service.org.directUrl", "http://localhost:7055/platform");
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getDataCatalogTree(String tenantId, String personId, boolean includeAll) throws Exception {
		// 创建CloseableHttpClient
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(serverUrl + "/services/rest/v1/dataCatalog/tree?treeType=dataflow&tenantId="+tenantId
				+"&includeAllDescendant="+includeAll+"&personId="+personId+"&authority=1");

		CloseableHttpResponse response = client.execute(httpGet);
		String obj = EntityUtils.toString(response.getEntity());
		Map<String, Object> map = Y9JsonUtil.readHashMap(obj);
		if((Boolean)map.get("success")) {
			return (List<Map<String, Object>>)map.get("data");
		}
		return null;
	}
	
	public static boolean hasRole(String tenantId, String personId, String roleName) throws Exception {
		// 创建CloseableHttpClient
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(serverUrl + "/services/rest/v1/personRole/hasRole3?systemName=dataflow&tenantId="+tenantId
				+"&roleName="+roleName+"&personId="+personId);

		CloseableHttpResponse response = client.execute(httpGet);
		String obj = EntityUtils.toString(response.getEntity());
		Map<String, Object> map = Y9JsonUtil.readHashMap(obj);
		if((Boolean)map.get("success")) {
			return (Boolean)map.get("data");
		}
		return false;
	}
	
	public static boolean hasRole2(String tenantId, String personId, String customId) throws Exception {
		// 创建CloseableHttpClient
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(serverUrl + "/services/rest/v1/personRole/hasRole2?tenantId="+tenantId
				+"&customId="+customId+"&personId="+personId);

		CloseableHttpResponse response = client.execute(httpGet);
		String obj = EntityUtils.toString(response.getEntity());
		Map<String, Object> map = Y9JsonUtil.readHashMap(obj);
		if((Boolean)map.get("success")) {
			return (Boolean)map.get("data");
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static String getNameById(String tenantId, String id) throws Exception {
		// 创建CloseableHttpClient
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(serverUrl + "/services/rest/v1/dataCatalog/get?tenantId="+tenantId+"&id="+id);

		CloseableHttpResponse response = client.execute(httpGet);
		String obj = EntityUtils.toString(response.getEntity());
		Map<String, Object> map = Y9JsonUtil.readHashMap(obj);
		if((Boolean)map.get("success")) {
			Map<String, Object> data = (Map<String, Object>) map.get("data");
			return (String) data.get("name");
		}
		return "";
	}
}

