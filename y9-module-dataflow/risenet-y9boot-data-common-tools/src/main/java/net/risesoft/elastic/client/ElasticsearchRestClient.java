package net.risesoft.elastic.client;

import net.risesoft.elastic.client.pojo.QueryModel;
import net.risesoft.elastic.utils.HttpClientEsUtil;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.json.Y9JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * elastic Rest API调用
 * @author pzx
 *
 */
public class ElasticsearchRestClient {
	
	private String url;// elastic连接地址
	private String username;// 用户名
	private String password;// 密码
	
	public ElasticsearchRestClient(String url, String username, String password) {
		super();
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * 获取所有索引
	 * @return
	 * @throws Exception
	 */
	public List<String> getIndexs() throws Exception {
		String data = HttpClientEsUtil.httpGet(url + "/_cat/indices?format=json", username, password);
		List<String> list = new ArrayList<String>();
		if(!data.equals("failed")) {
			List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(data);
			for(Map<String, Object> map : listMap) {
				if("yellow".equals(map.get("health").toString())) {
					list.add(map.get("index").toString());
				}
			}
		}
    	return list;
    }
  
    /**
     * 根据索引名称获取映射
     * @param indexName
     * @return {"my_index":{"mappings":{"properties":{"age":{"type":"keyword"},"name":{"type":"text","analyzer":"ik_smart"}}}}}
     * @throws Exception
     */
    public String getMapping(String indexName) throws Exception {
    	return HttpClientEsUtil.httpGet(url + "/" + indexName + "/_mapping", username, password);
    }
    
    /**
     * 创建索引
     * @param indexName
     * @throws Exception
     */
    public String createIndex(String indexName) throws Exception {
    	return HttpClientEsUtil.httpPut("", url + "/" + indexName, username, password);
    }
  
    /**
     * 创建映射
     * @param indexName
     * @param mapping {"properties":{"age":{"type":"keyword"},"name":{"type":"text","analyzer":"ik_smart"}}}
     * @throws IOException
     */
    public String createMapping(String indexName, String mapping) throws Exception {
    	return HttpClientEsUtil.httpPost(mapping, url + "/" + indexName + "/_mapping", username, password);
    }
    
    /**
     * 添加新映射字段
     * @param indexName
     * @param mapping {"properties":{"age":{"type":"keyword"}}}
     * @throws Exception
     */
    public String addMapping(String indexName, String mapping) throws Exception {
    	return HttpClientEsUtil.httpPut(mapping, url + "/" + indexName + "/_mapping", username, password);
    }
    
    /**
     * 创建索引和映射字段
     * @param indexName
     * @param mapping {"settings": {},"mappings":{"properties":{"age":{"type":"keyword"},"name":{"type":"text","analyzer":"ik_smart"}}}}
     * @return
     * @throws Exception
     */
    public String createIndexAndMapping(String indexName, String mapping) throws Exception {
    	return HttpClientEsUtil.httpPut(mapping, url + "/" + indexName, username, password);
    }
    
    /**
     * 复制表和数据
     * @param indexName
     * @param newIndexName
     * @param isData 是否拷贝数据
     * @return
     * @throws Exception
     */
    public Y9Result<String> copyIndexData(String indexName, String newIndexName, boolean isData) throws Exception {
    	// 获取原表结构
    	String json = getMapping(indexName);
    	if(json.equals("failed")) {
    		return Y9Result.failure("获取原表结构失败");
    	}
    	Map<String, Object> data = Y9JsonUtil.readHashMap(json);
    	// 创建索引表
    	String msg = createIndexAndMapping(newIndexName, Y9JsonUtil.writeValueAsString(data.get(indexName)));
    	if(msg.equals("failed")) {
    		return Y9Result.failure("创建索引表失败");
    	}
    	if(isData) {
    		// 迁移数据
        	String mapping = "{\"source\": {\"index\": \""+indexName+"\"},\"dest\": {\"index\": \""+newIndexName+"\"}}";
        	String response = HttpClientEsUtil.httpPost(mapping, url + "/_reindex", username, password);
        	if(response.equals("failed")) {
        		return Y9Result.failure("复制数据失败");
        	}
    	}
    	return Y9Result.successMsg("操作成功");
    }
    
    /**
     * 新增/修改文档
     * @param indexName
     * @param document {"field1":"value1","field2":"value2"}
     * @param id 主键id
     * @return
     * @throws Exception
     */
    public String addDocument(String indexName, String document, String id) throws Exception {
    	String data = HttpClientEsUtil.httpPost(document, url + "/" + indexName + "/_doc/" + id, username, password);
    	if(!data.equals("failed")) {
    		return data;
    	}else {
    		throw new Exception("新增/修改文档失败");
    	}
    }
    
    /**
     * 根据id获取文档
     * @param id
     * @param indexName
     * @return
     * @throws Exception
     */
    public String getDocument(String id, String indexName) throws Exception {
    	String response = HttpClientEsUtil.httpGet(url + "/" + indexName + "/_doc/" + id, username, password);
    	if(response.equals("failed")) {
    		throw new Exception("根据id获取文档失败");
    	}
    	Map<String, Object> map = Y9JsonUtil.readHashMap(response);
    	String data = Y9JsonUtil.writeValueAsString(map.get("_source"));
    	return data;
    }
    
    /**
     * 获取索引表数据量
     * @param indexName
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public int getCount(String indexName, String query) throws Exception {
    	String data = HttpClientEsUtil.httpPost(query, url + "/" + indexName + "/_search", username, password);
    	if(!data.equals("failed")) {
    		Map<String, Object> map = Y9JsonUtil.readHashMap(data);
    		Integer total = 0;
    		Map<String, Object> hits = (Map<String, Object>) map.get("hits");
    		if(hits != null) {
    			try {
					total = (Integer) hits.get("total");
				} catch (Exception e) {
					Map<String, Object> totalMap = (Map<String, Object>) hits.get("total");
					total = (Integer) totalMap.get("value");
				}
    		}
    		return total;
    	}else {
    		throw new Exception("获取索引表数据量失败");
    	}		
    }
    
    /**
     * 查询数据
     * @param queryModel
     * @param indexName
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public Map<String, Object> search(QueryModel queryModel, String indexName) throws Exception {
    	String queryType = "query";
    	String queryJson = "{";
    	if(StringUtils.isNotBlank(queryModel.getQuery())) {
    		queryJson += "\"query\":" + queryModel.getQuery();
    	}
    	if(StringUtils.isNotBlank(queryModel.getAggs())) {
    		queryType = "aggs";
    		queryJson += (queryJson.equals("{")?"":",") + "\"aggs\":" + queryModel.getAggs();
    	}
    	if(StringUtils.isNotBlank(queryModel.get_source())) {
    		queryJson += (queryJson.equals("{")?"":",") + "\"_source\":" + queryModel.get_source();
    	}
    	if(StringUtils.isNotBlank(queryModel.getSort())) {
    		queryJson += (queryJson.equals("{")?"":",") + "\"sort\":" + queryModel.getSort();
    	}
    	if(queryModel.getFrom() != null) {
    		queryJson += (queryJson.equals("{")?"":",") + "\"from\":" + queryModel.getFrom();
    	}
    	if(queryModel.getSize() != null) {
    		queryJson += (queryJson.equals("{")?"":",") + "\"size\":" + queryModel.getSize();
    	}
    	queryJson += "}";
    	String response = HttpClientEsUtil.httpPost(queryJson, url + "/" + indexName + "/_search", username, password);
    	if(response.equals("failed")) {
    		throw new Exception("索引表-" + indexName + "查询报错，请检查查询接口报错信息");
    	}else {
    		Map<String, Object> map = new HashMap<String, Object>();
    		Map<String, Object> data = Y9JsonUtil.readHashMap(response);
        	if(queryType.equals("aggs")) {
        		Map<String, Object> hits = (Map<String, Object>) data.get("aggregations");
        		if(hits != null) {
        			Map<String, Object> aggsMap = (Map<String, Object>) hits.get("aggs_name");
        			// 判断是否分组查询，分组返回list
        			List<Map<String, Object>> aggs_list = (List<Map<String, Object>>) aggsMap.get("buckets");
        			if(aggs_list == null) {
        				map.put("data", aggsMap.get("value"));
        			}else {
        				map.put("data", aggs_list);
        			}
        		}
        	}else {
        		Integer total = 0;
        		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
        		Map<String, Object> hits = (Map<String, Object>) data.get("hits");
        		if(hits != null) {
        			try {
    					total = (Integer) hits.get("total");
    				} catch (Exception e) {
    					Map<String, Object> totalMap = (Map<String, Object>) hits.get("total");
    					total = (Integer) totalMap.get("value");
    				}
        			List<Map<String, Object>> hits_list = (List<Map<String, Object>>) hits.get("hits");
        			for(Map<String, Object> hit : hits_list) {
        				listMap.add((Map<String, Object>) hit.get("_source"));
        			}
        		}
        		map.put("total", total);
        		map.put("data", listMap);
        	}
        	return map;
    	}
    }
}
