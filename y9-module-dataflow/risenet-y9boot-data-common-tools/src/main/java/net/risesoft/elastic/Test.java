package net.risesoft.elastic;

import java.util.List;
import java.util.Map;

import net.risesoft.elastic.utils.HttpClientEsUtil;
import net.risesoft.y9.json.Y9JsonUtil;

public class Test {

	public static void main(String[] args) {
		try {
			//String mapping = "{\"settings\": {},\"mappings\":{\"properties\":{\"age\":{\"type\":\"keyword\"},\"name\":{\"type\":\"text\",\"analyzer\":\"ik_smart\"}}}}";
			//HttpClientEsUtil.httpPut(mapping, "http://localhost:9200/test", "", "");
			String mapping = "{\"index\":{\"_id\":\"1\"}}\r\n" + 
					"{\"name\": \"Jack\",\"age\":\"20\" }\r\n" + 
					"{\"index\":{\"_id\":\"3\"}}\r\n" + 
					"{\"name\": \"Jane\",\"age\":\"25\"}\r\n";
			String data = HttpClientEsUtil.httpPost(mapping, "http://localhost:9200/test/_doc/_bulk", "", "");
			System.out.println(data);
//			String queryJson = "{\"_source\":{\"includes\":[\"userId\",\"userLoginName\",\"userName\"]},\"sort\":{\"loginTime\":{\"order\":\"asc\"}},\"from\":65,\"size\":66}";
//			String response = HttpClientEsUtil.httpPost(queryJson, "http://localhost:9200/userlogininfo/_search", "", "");
//			Map<String, Object> data = Y9JsonUtil.readHashMap(response);
//			Map<String, Object> hits = (Map<String, Object>) data.get("hits");
//			List<Map<String, Object>> hits_list = (List<Map<String, Object>>) hits.get("hits");
//			for(Map<String, Object> hit : hits_list) {
//				System.out.println((Map<String, Object>) hit.get("_source"));
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
