package net.risesoft.pojo;

import lombok.Data;

/**
 * elastic查询参数
 * @author pzx
 *
 */
@Data
public class QueryModel {

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
	 * 3.分组统计查询：{"aggs_name":{"terms":{"field":"age"}}}
	 */
	private String aggs;
	
	/**
	 * 返回指定字段：{"includes":["name","age"]}
	 */
	private String _source;
	
	/**
	 * 当前页的起始索引，默认从0开始。 from = (pageNum - 1) * size
	 */
	private Integer from;
	
	/**
	 * 每页返回数
	 */
	private Integer size;
	
	/**
	 * 排序：[{"date":{"order":"asc"}},{"tabIndex":{"order":"desc"}}]
	 */
	private String sort;
	
}