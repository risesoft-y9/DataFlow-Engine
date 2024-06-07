package risesoft.data.transfer.stream.es.utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 提供es的一套操作
 * 
 * @author libo 2021年7月14日
 */
public class EsTemplate {
	private Map<String, String> header;
	private String url;

	/**
	 * @param header
	 * @param userName
	 * @param password
	 */
	public EsTemplate(String userName, String password, String host) {
		this.header = new HashMap<String, String>();
		header.put("Authorization", "Basic " + ChineseMessyCodeSolution.strToBase64(userName + ":" + password));
		this.url = host;
	}

	private static final String QUERY = "{" + "  \"query\": {  %s  } }";
	private static final String QUERY_FIELD = "{ \"_source\":%s,  \"query\": {  %s  } }";
	private static final String QUERYNO = "{" + "  \"query\":   %s   }";

	/**
	 * 保存
	 * 
	 * @param index 索引
	 * @param type  类型
	 * @param id    id
	 * @param obj   对象
	 * @return
	 */
	public int save(String index, String type, String id, Object obj) {
		JSONObject json = postSend(url + index + "/" + type + "/" + id, JSON.toJSONString(obj));
		return "result".equals(json.getString("result")) ? 0 : 1;
	}

	/**
	 * 更新返回更新是否成功
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param obj
	 * @return
	 */
	public boolean update(String index, String type, String id, Object obj) {
		JSONObject jsonObject = postSend(url + index + "/" + type + "/" + id + "/_update",
				"{\"doc\":" + JSON.toJSONString(obj) + "}");

		return jsonObject.getString("result").equals("updated");
	}

	/**
	 * 保存
	 * 
	 * @param index 索引
	 * @param type  类型
	 * @param id    id
	 * @param obj   对象
	 * @return
	 */
	public int save(String index, String id, Object obj) {
		return save(index, "_doc", id, obj);
	}

	/**
	 * 根据id拿到一个返回值
	 * 
	 * @param <T>
	 * @param index      索引
	 * @param id         id
	 * @param returnType 返回值
	 * @return
	 */
	public <T> T getOne(String index, String type, String id, Class<T> returnType) {
		JSONObject json = getSend(url + index + "/" + type + "/" + id, "");
		if (json.getBooleanValue("found")) {
			return json.getJSONObject("_source").toJavaObject(returnType);
		}
		return null;
	}

	/**
	 * 根据id删除
	 * 
	 * @param index 索引
	 * @param id    id
	 * @return
	 */
	public int deleteById(String index, String type, String id) {
		JSONObject json = deleteSend(url + index + "/" + type + "/" + id, "");
		if ("deleted".equals(json.getString("result"))) {
			return json.getJSONObject("_shards").getIntValue("successful");
		}
		return 0;
	}

	/**
	 * 删除使用match 匹配
	 * 
	 * @param index
	 * @param queryMap
	 * @return
	 */
	public int delteForMatch(String index, Map<String, Object> queryMap) {
		return deleteForQuery(index, "match", queryMap);
	}

	/**
	 * 删除使用Term 单匹配
	 * 
	 * @param index
	 * @param queryMap
	 * @return
	 */
	public int delteForTerm(String index, Map<String, Object> queryMap) {
		return deleteForQuery(index, "term", queryMap);
	}

	/**
	 * 删除使用Terms 方法多匹配
	 * 
	 * @param index
	 * @param queryMap
	 * @return
	 */
	public int delteForTerms(String index, Map<String, Object> queryMap) {

		return deleteForQuery(index, "terms", queryMap);
	}

	/**
	 * 根据query方法删除
	 * 
	 * @param index       索引
	 * @param queryMethod 查询方法
	 * @param queryMap    参数
	 * @return
	 */
	public int deleteForQuery(String index, String queryMethod, Map<String, Object> queryMap) {
		JSONObject json = postSend(url + index + "/_delete_by_query",
				String.format(QUERY, '"' + queryMethod + '"' + ":" + JSON.toJSONString(queryMap)));
		return json.getIntValue("deleted");
	}

	/**
	 * 根据查询条件查询 使用查询方法是Match
	 * 
	 * @param <T>
	 * @param index      索引
	 * @param queryMap   条件的map
	 * @param returnType 返回对象
	 * @return
	 */
	public <T> List<T> queryMatchForList(String index, Map<String, Object> queryMap, Class<T> returnType) {
		return query(index, "match", queryMap, returnType);
	}

	/**
	 * 根据查询条件查询 使用查询方法是wildcard
	 * 
	 * @param <T>
	 * @param index      索引
	 * @param queryMap   条件的map
	 * @param returnType 返回对象
	 * @return
	 */
	public <T> List<T> queryWildcardForList(String index, Map<String, Object> queryMap, Class<T> returnType) {
		return query(index, "wildcard", queryMap, returnType);
	}

	/**
	 * 根据查询条件查询 查询方法自己定义
	 * 
	 * @param <T>
	 * @param index       索引
	 * @param queryMethod 查询的方法
	 * @param queryMap    查询参数
	 * @param returnType  返回对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> query(String index, String queryMethod, Map<String, Object> queryMap, Class<T> returnType) {
		JSONObject json = postSend(url + index + "/_search",
				String.format(QUERY, '"' + queryMethod + '"' + ":" + JSON.toJSONString(queryMap)));

		json = json.getJSONObject("hits");
		if (json.getIntValue("total") > 0) {
			JSONArray jsonArray = json.getJSONArray("hits");
			List<Object> res = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				res.add(jsonArray.getJSONObject(i).getJSONObject("_source").toJavaObject(returnType));
			}
			return (List<T>) res;
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * 根据查询条件查询 查询方法自己定义
	 * 
	 * @param <T>
	 * @param index       索引
	 * @param queryMethod 查询的方法
	 * @param queryMap    查询参数
	 * @param returnType  返回对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> query(String index, String[] fields, String[] collapse, String queryMethod,
			Map<String, Object> queryMap, Class<T> returnType) {

		JSONObject json = postSend(url + index + "/_search", String.format(QUERY_FIELD, JSON.toJSONString(fields),
				'"' + queryMethod + '"' + ":" + JSON.toJSONString(queryMap)));

		json = json.getJSONObject("hits");
		if (json.getIntValue("total") > 0) {
			JSONArray jsonArray = json.getJSONArray("hits");
			List<Object> res = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				res.add(jsonArray.getJSONObject(i).getJSONObject("_source").toJavaObject(returnType));
			}
			return (List<T>) res;
		} else {
			return new ArrayList<>();
		}
	}

	private String appendArgs(String[] fields, Map<String, Object> collapse, Map<String, Object> queryMap,
			Map<String, Object> highlight, Integer from, Integer size, Map<String, Object>[] sorts) {
		Map<String, Object> args = new HashMap<>();
		if (fields != null && fields.length > 0) {
			args.put("_source", fields);
		}
		if (collapse != null && collapse.size() > 0) {
			args.put("collapse", collapse);
		}
		if (queryMap != null && queryMap.size() > 0) {
			args.put("query", queryMap);
		}
		if (highlight != null && highlight.size() > 0) {
			args.put("highlight", LMap.toMap("fields", highlight));
		}
		if (from != null) {
			args.put("from", from);
		}
		if (size != null) {
			args.put("size", size);
		}
		if (sorts != null && sorts.length > 0) {
			args.put("sort", sorts);
		}

		return JSONObject.toJSONString(args);
	}

	/**
	 * 
	 * @param <T>
	 * @param index
	 * @param fields     查询的字段 空则是全部
	 * @param collapse   去重
	 * @param queryMap   查询的map
	 * @param pageable   分页数据
	 * @param highlight  高亮
	 * @param returnType
	 * @return
	 */
	public <T> LPage<T> searchForPage(String index, String[] fields, Map<String, Object> collapse,
			Map<String, Object> queryMap, LPageable pageable, Map<String, Object> highlight, Class<T> returnType) {

		LPage<T> page = new LPage<>();
		Map<String, Object> searchMap = new HashMap<>();
		boolean isHighlight = highlight != null && highlight.size() > 0;
		Map<String, Object>[] sorts = null;
		LSort sort = pageable.getSort();
		if (sort != null && sort.getOrders() != null) {
			sorts = new HashMap[sort.getOrders().size()];
			for (int i = 0; i < sort.getOrders().size(); i++) {
				sorts[i] = LMap.toMap(sort.getOrders().get(i).getField(),
						LMap.toMap("order", sort.getOrders().get(i).getOrder()));
			}
			searchMap.put("sort", sorts);
		}
		JSONObject json = postSend(url + index + "/_search", appendArgs(fields, collapse, queryMap, highlight,
				(pageable.getPageNo() - 1) * pageable.getPageSize(), pageable.getPageSize(), sorts));
		if (json.containsKey("status")) {
			throw new RuntimeException("查询es 出错" + json);
		}
		json = json.getJSONObject("hits");
		page.setPageno(pageable.getPageNo());
		page.setPagesize(pageable.getPageSize());
		if (json == null || !json.containsKey("total")) {
			page.setTotal(0);
			return page;
		}
		Long total = json.getLongValue("total");
		page.setTotal(total);
		if (total > 0) {
			JSONArray jsonArray = json.getJSONArray("hits");
			List<Object> res = new ArrayList<>();
			JSONObject detail;
			JSONObject source;
			JSONObject highlightJson;
			Set<String> keys;
			for (int i = 0; i < jsonArray.size(); i++) {
				detail = jsonArray.getJSONObject(i);
				source = detail.getJSONObject("_source");
				if (isHighlight) {
					highlightJson = detail.getJSONObject("highlight");
					if (highlightJson != null) {
						keys = highlightJson.keySet();
						for (String key : keys) {
							source.put(key, highlightJson.getJSONArray(key).get(0));
						}
					}
				}
				res.add(source.toJavaObject(returnType));
			}
			page.setContent((List<T>) res);
			return page;
		}
		page.setContent(new ArrayList<>());
		return page;
	}

	@SuppressWarnings("unchecked")
	public <T> LPage<T> searchForPage(String index, Map<String, Object> queryMap, LPageable pageable,
			Map<String, Object> highlight, Class<T> returnType) {
		LPage<T> page = new LPage<>();
		Map<String, Object> searchMap = new HashMap<>();
		boolean isHighlight = highlight != null && highlight.size() > 0;
		if (isHighlight) {
			searchMap.put("highlight", LMap.toMap("fields", highlight));
		}
		searchMap.put("from", (pageable.getPageNo() - 1) * pageable.getPageSize());
		searchMap.put("size", pageable.getPageSize());
		searchMap.put("query", queryMap);
		LSort sort = pageable.getSort();
		if (sort != null && sort.getOrders() != null) {
			Map<String, Object>[] sorts = new HashMap[sort.getOrders().size()];
			for (int i = 0; i < sort.getOrders().size(); i++) {
				sorts[i] = LMap.toMap(sort.getOrders().get(i).getField(),
						LMap.toMap("order", sort.getOrders().get(i).getOrder()));
			}
			searchMap.put("sort", sorts);
		}
		JSONObject json = postSend(url + index + "/_search", JSON.toJSONString(searchMap));
		if (json.containsKey("status")) {
			throw new RuntimeException("查询es 出错" + json);
		}
		json = json.getJSONObject("hits");
		page.setPageno(pageable.getPageNo());
		page.setPagesize(pageable.getPageSize());
		if (json == null || !json.containsKey("total")) {
			page.setTotal(0);
			return page;
		}
		Long total = json.getLongValue("total");
		page.setTotal(total);
		if (total > 0) {
			JSONArray jsonArray = json.getJSONArray("hits");
			List<Object> res = new ArrayList<>();
			JSONObject detail;
			JSONObject source;
			JSONObject highlightJson;
			Set<String> keys;
			for (int i = 0; i < jsonArray.size(); i++) {
				detail = jsonArray.getJSONObject(i);
				source = detail.getJSONObject("_source");
				if (isHighlight) {
					highlightJson = detail.getJSONObject("highlight");
					if (highlightJson != null) {
						keys = highlightJson.keySet();
						for (String key : keys) {
							source.put(key, highlightJson.getJSONArray(key).get(0));
						}
					}
				}
				res.add(source.toJavaObject(returnType));
			}
			page.setContent((List<T>) res);
			return page;
		}
		page.setContent(new ArrayList<>());
		return page;
	}

	private JSONObject postSend(String url, String arg) {
		String res = sendToEs(url, "POST", arg);
		return JSON.parseObject(res);
	}

	private JSONObject putSend(String url, String arg) {
		String res = sendToEs(url, "PUT", arg);
		return JSON.parseObject(res);
	}

	private JSONObject getSend(String url, String arg) {
		String res = sendToEs(url, "GET", arg);
		return JSON.parseObject(res);
	}

	private JSONObject deleteSend(String url, String arg) {
		String res = sendToEs(url, "DELETE", arg);
		return JSON.parseObject(res);
	}

	private String sendToEs(String url, String method, String arg) {
		String str = null;
		try {
			str = HttpClientUtil.sendRequestByRaw(method, url, arg, "UTF-8", "application/json", 20000, true, header);
			return str;
		} catch (Exception e) {
			throw new RuntimeException("es异常:" + e.getMessage() + str);
		}

	}

	/**
	 * 获取count
	 * 
	 * @param index
	 * @param queryMap
	 * @return
	 */
	public Integer getCount(String index, String queryMap) {
		String json = sendToEs(url + index + "/_count", "post", queryMap);
		try {
			JSONObject jsonObject = JSON.parseObject(json);
			return jsonObject.getInteger("count");
		} catch (Exception e) {
			throw new RuntimeException("获取count 失败" + e.getMessage());
		}

	}
}
