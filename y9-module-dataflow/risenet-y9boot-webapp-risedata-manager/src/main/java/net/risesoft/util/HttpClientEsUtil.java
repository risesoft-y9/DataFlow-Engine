package net.risesoft.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import net.risesoft.y9.util.base64.Y9Base64Util;

/**
 * elastic HTTP请求
 * @author pzx
 *
 */
public class HttpClientEsUtil {
	
	public static String httpPost(String mapping, String url, String username, String password) throws IOException {  
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {  
            HttpPost httpPost = new HttpPost(url);
            
            //构造Authorization
            if(StringUtils.isNotBlank(username)) {
            	String auth = username + ":" + password;
                httpPost.setHeader("Authorization", "Basic " + Y9Base64Util.encode(auth.getBytes()));
            }
  
            // 设置请求体，包含索引的映射定义
            if(StringUtils.isNotBlank(mapping)) {
            	StringEntity requestEntity = new StringEntity(mapping, ContentType.APPLICATION_JSON);  
                httpPost.setEntity(requestEntity); 
            }
  
            // 发送请求并获取响应  
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {  
                HttpEntity responseEntity = response.getEntity();  
                if (responseEntity != null) {  
                    String responseBody = EntityUtils.toString(responseEntity, "UTF-8");  
                    // 检查响应状态码，确认索引是否成功创建  
                    if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {  
                    	return responseBody;
                    } else {  
                        System.out.println(responseBody);  
                    }  
                }  
            }  
        }
        return "failed";
    }
	
	public static String httpPut(String mapping, String url, String username, String password) throws IOException {  
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {  
        	HttpPut httpPut = new HttpPut(url);
            
            //构造Authorization
        	if(StringUtils.isNotBlank(username)) {
            	String auth = username + ":" + password;
            	httpPut.setHeader("Authorization", "Basic " + Y9Base64Util.encode(auth.getBytes()));
            }
  
            // 设置请求体，包含索引的映射定义
            if(StringUtils.isNotBlank(mapping)) {
            	StringEntity requestEntity = new StringEntity(mapping, ContentType.APPLICATION_JSON);  
            	httpPut.setEntity(requestEntity); 
            }
  
            // 发送请求并获取响应  
            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {  
                HttpEntity responseEntity = response.getEntity();  
                if (responseEntity != null) { 
                	String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
                    // 检查响应状态码，确认索引是否成功创建  
                    if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {  
                    	return responseBody;
                    } else {  
                        System.out.println(responseBody);  
                    }  
                }  
            }  
        }
        return "failed";
    }
	
	public static String httpGet(String url, String username, String password) throws IOException {  
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {  
        	HttpGet httpGet = new HttpGet(url);
            
            //构造Authorization
        	if(StringUtils.isNotBlank(username)) {
            	String auth = username + ":" + password;
            	httpGet.setHeader("Authorization", "Basic " + Y9Base64Util.encode(auth.getBytes()));
            }
  
            // 发送请求并获取响应  
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {  
                HttpEntity responseEntity = response.getEntity();  
                if (responseEntity != null) {
                	String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
                    // 检查响应状态码，确认索引是否成功创建  
                    if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
                        return responseBody;
                    } else {
                    	System.out.println(responseBody);
                    }  
                }  
            }  
        }
        return "failed";
    }

}
