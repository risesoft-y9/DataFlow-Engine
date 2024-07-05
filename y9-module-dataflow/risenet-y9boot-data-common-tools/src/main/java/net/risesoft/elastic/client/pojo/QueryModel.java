package net.risesoft.elastic.client.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * elastic查询参数
 * @author pzx
 *
 */
@Data
public class QueryModel {
	
	public static final String MATCH = "match";
	
	public static final String MATCH_PHRASE = "match_phrase";
	
	public static final String TERM = "term";
	
	public static final String TERMS = "terms";
	
	public static final String FUZZY = "fuzzy";

	/**
	 * 查询语句
	 * <p>
	 * 1.字段匹配查询：{"match":{"name":"张三"}}，    "match_phrase"和"match"类似
	 * <p>
	 * 2.匹配多个字段：{"multi_match":{"query":"1","fields":["name","age"]}}
	 * <p>
	 * 3.精确匹配查询（非text类型字段）：{"term":{"name":{"value":"张三"}}}，in查询：{"terms":{"name":["张","张三"]}}
	 * <p>
	 * 4.模糊查询：{"fuzzy":{"name":{"value":"张"}}}
	 * <p>
	 * 5.组合查询(must/must_not/should)：{"bool":{"must":[{"match":{"name":"张三"}},{"match":{"age":11}}]}}
	 * <p>
	 * 6.范围查询（需是组合查询，gt：大于、gte：大于等于、lt：小于、lte：小于等于）：{"filter":{"range":{"age":{"gt":20,"lt":30}}}}
	 */
	private String query;
	
	/**
	 * 聚合查询：
	 * <p>
	 * 1.查询字段最大值max：{"aggs_name":{"max":{"field":"age"}}}，最小值min，求和sum，平均值avg
	 * <p>
	 * 2.对字段去重取数量：{"aggs_name":{"cardinality":{"field":"age"}}}
	 * <p>
	 * 3.分组统计查询：{"aggs_name":{"terms":{"field":"age", "size":10 //只返回前10个桶}}}
	 */
	private String aggs;
	
	/**
	 * 返回指定字段：{"includes":["name","age"]}，聚合查询不起作用
	 */
	private String _source;
	
	/**
	 * 当前页的起始索引，默认从0开始，聚合查询时不设。 from = (pageNum - 1) * size
	 */
	private Integer from;
	
	/**
	 * 每页返回数，聚合查询时设置size: 0表示不返回文档结果，而只返回聚合结果
	 */
	private Integer size;
	
	/**
	 * 排序：[{"date":{"order":"asc"}},{"tabIndex":{"order":"desc"}}]
	 */
	private String sort;
	
	/**
	 * 获取返回指定字段参数值
	 * @param columnList
	 * @return
	 */
	public static String get_source(List<String> columnList) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("includes", columnList);
		return Y9JsonUtil.writeValueAsString(map);
	}
	
	/**
	 * 获取查询语句
	 * @param field 字段名称
	 * @param value 字段值
	 * @param queryType 查询类型 match/term/fuzzy/...
	 * @return
	 */
	public static String get_query(String field, String value, String queryType) {
		return "{\"" + queryType + "\":{\"" + field + "\":\"" + value + "\"}}";
	}
	
	/**
	 * 获取组合查询must
	 * @param queryMap [{"match":{"name":"张三"}},{"match":{"age":11}}]
	 * @return
	 */
	public static String get_boolMustQuery(List<Map<String, Object>> queryMap) {
		return "{\"bool\":{\"must\":" + Y9JsonUtil.writeValueAsString(queryMap) + "}}";
	}
	
	/**
	 * 获取组合查询must_not
	 * @param queryMap [{"match":{"name":"张三"}},{"match":{"age":11}}]
	 * @return
	 */
	public static String get_boolMustNotQuery(List<Map<String, Object>> queryMap) {
		return "{\"bool\":{\"must_not\":" + Y9JsonUtil.writeValueAsString(queryMap) + "}}";
	}
	
	/**
	 * 获取组合查询should
	 * @param queryMap [{"match":{"name":"张三"}},{"match":{"age":11}}]
	 * @return
	 */
	public static String get_boolShouldQuery(List<Map<String, Object>> queryMap) {
		return "{\"bool\":{\"should\":" + Y9JsonUtil.writeValueAsString(queryMap) + "}}";
	}
	
}