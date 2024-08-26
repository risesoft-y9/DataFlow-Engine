package net.risesoft.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;

import net.risesoft.model.RequestModel;

public class ApiTest {
	
	public static String sendApi(RequestModel requestModel) {
		String response = "";
		String url = requestModel.getUrl();
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		if("GET".equals(requestModel.getMethod())){
			HttpMethod method = new GetMethod();
			try {
				//获取请求头信息
				List<Map<String, Object>> headers = requestModel.getHeaders();
				for(Map<String, Object> hmap : headers){
					String name = (String) hmap.get("name");
					if(StringUtils.isBlank(name)) {
						continue;
					}
					method.addRequestHeader(name, (String) hmap.get("value"));
				}
				String str = "";
				List<Map<String, Object>> params = requestModel.getParams();
				for(Map<String, Object> pmap : params){
					String name = (String) pmap.get("name");
					if(StringUtils.isBlank(name)) {
						continue;
					}
					str = str + name + "=" + URLEncoder.encode((String) pmap.get("value"), "UTF-8") + "&";
				}
				if(StringUtils.isNotBlank(str)) {
					url = url + "?" + str.substring(0, str.length() - 1);
				}
				method.setPath(url);
				int httpCode = client.executeMethod(method);
				if (httpCode == HttpStatus.SC_OK) {
					InputStream resStream = method.getResponseBodyAsStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(resStream, "UTF-8"));
					StringBuffer resBuffer = new StringBuffer();
					String resTemp = "";
					while ((resTemp = br.readLine()) != null) {
						resBuffer.append(resTemp);
					}
					response = resBuffer.toString();
				}else {
					response = "接口请求失败--" + httpCode;
				}
			} catch (Exception e) {
				response = "程序出错--" + e.getMessage();
			} finally {
				method.releaseConnection();
				((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
			}
		}
		else if("POST".equals(requestModel.getMethod())){
			PostMethod method = new PostMethod();
			try {
				//获取请求头信息
				List<Map<String, Object>> headers = requestModel.getHeaders();
				for(Map<String, Object> hmap : headers){
					String name = (String) hmap.get("name");
					if(StringUtils.isBlank(name)) {
						continue;
					}
					method.addRequestHeader(name, (String) hmap.get("value"));
				}
				//获取请求参数
				List<Map<String, Object>> params = requestModel.getParams();
				for(Map<String, Object> pmap : params){
					String name = (String) pmap.get("name");
					if(StringUtils.isBlank(name)) {
						continue;
					}
					method.addParameter((String) pmap.get("name"), (String) pmap.get("value"));
				}
				if(StringUtils.isNotBlank(requestModel.getBody())){
					//主体body
					StringRequestEntity requestEntity = new StringRequestEntity(requestModel.getBody(), 
							requestModel.getContentType(), "UTF-8");
					method.setRequestEntity(requestEntity);
				}
				method.setPath(url);
				int httpCode = client.executeMethod(method);
				if (httpCode == HttpStatus.SC_OK) {
					InputStream resStream = method.getResponseBodyAsStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(resStream, "UTF-8"));
					StringBuffer resBuffer = new StringBuffer();
					String resTemp = "";
					while ((resTemp = br.readLine()) != null) {
						resBuffer.append(resTemp);
					}
					response = resBuffer.toString();
				}else {
					response = "接口请求失败--" + httpCode;
				}
			} catch (Exception e) {
				response = "程序出错--" + e.getMessage();
			}  finally {
				method.releaseConnection();
				((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
			}
		}
		return response;
	}

}
