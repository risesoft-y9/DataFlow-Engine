package risesoft.data.transfer.stream.es.in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.elastic.client.ElasticsearchRestClient;
import net.risesoft.elastic.client.pojo.QueryModel;
import net.risesoft.y9.json.Y9JsonUtil;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.data.StringData;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.FrameworkErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.stream.in.DataInputStream;
import risesoft.data.transfer.core.stream.in.DataInputStreamFactory;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ValueUtils;

/**
 * elastic输入流工厂
 * @author pzx
 *
 */
public class ElasticsearchInputStreamFactory implements DataInputStreamFactory {

	private ElasticsearchRestClient elasticsearchRestClient;
	private Logger logger;

	private String url;// 连接地址
	private String username;// 用户名
	private String password;// 密码
	private String indexName;// 索引表
	private List<String> columns;// 字段列表
	private String query;// 查询语句
	private String splitPk;// 切分字段
	private Boolean precise;// 是否精准切分
	private Integer tableNumber;// 切分为多少块
	private Integer splitFactor;// 切分系数
	
	private Map<String, String> columnTypes;// 字段类型

	public ElasticsearchInputStreamFactory(Configuration configuration, LoggerFactory loggerFactory) {
		this.url = ValueUtils.getRequired(configuration.getString("jdbcUrl"), "缺失连接地址");
		this.password = configuration.getString("password", "");
		this.username = configuration.getString("userName", "");
		this.indexName = ValueUtils.getRequired(configuration.getString("tableName"), "缺失索引表名称");
		this.columns = ValueUtils.getRequired(configuration.getList("column", String.class), "缺失字段列表");
		this.query = configuration.getString("where", "").trim();
		this.splitPk = configuration.getString("splitPk");
		this.precise = configuration.getBool("precise", false);
		this.splitFactor = configuration.getInt("splitFactor", -1);
		this.tableNumber = configuration.getInt("tableNumber", -1);
		logger = loggerFactory.getLogger(configuration.getString("name", "ElasticsearchInputStreamFactory"));
		elasticsearchRestClient = new ElasticsearchRestClient(url, username, password);
	}

	@Override
	public DataInputStream getStream() {
		return new ElasticsearchInputStream(elasticsearchRestClient, indexName, columnTypes, logger);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		columnTypes = new HashMap<String, String>();
		try {
			// 获取字段类型
			String data = elasticsearchRestClient.getMapping(indexName);
			if(!data.equals("failed")) {
				Map<String, Object> map = Y9JsonUtil.readHashMap(data);
				Map<String, Object> mappings = (Map<String, Object>) map.get(indexName);
				Map<String, Object> properties = (Map<String, Object>) mappings.get("mappings");
				Map<String, Object> dataMap = (Map<String, Object>) properties.get("properties");
				// 低版本的es存在type值，需要先获取type
				if(dataMap == null) {
					Map<String, Object> type = (Map<String, Object>) properties.values().stream().findFirst().get();
					dataMap = (Map<String, Object>) type.get("properties");
				}
				for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
					String column_name = entry.getKey();// 获得字段名
					// 字段详情
					Map<String, Object> value = (Map<String, Object>) entry.getValue();
					// 存储字段信息
					columnTypes.put(column_name, (String)value.get("type"));
				}
			}else {
				throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "初始化ES输入流工厂失败：获取索引表字段信息失败");
			}
		} catch (Exception e) {
			throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "初始化ES输入流工厂失败，异常信息：" + e.getMessage(), e);
		}
	}

	@Override
	public void close() throws Exception {

	}

	@Override
	public List<Data> splitToData(int executorSize) throws Exception {
		List<Data> querys = null;
		// 获取表数量
		int count = 0;
		try {
			String queryJson = "{";
			if(StringUtils.isNotBlank(query)) {
				queryJson += "\"query\":" + query;
			}
			queryJson += "}";
			count = elasticsearchRestClient.getCount(indexName, queryJson);
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "elastic数量查询-执行报错", e);
		}
		// 判断是否设置了切分字段
		int numberSize = tableNumber != -1 ? tableNumber : this.splitFactor != -1 ? executorSize * splitFactor : 1;
		boolean isSub = numberSize >= 1 && StringUtils.isNotEmpty(this.splitPk);
		if (isSub) {
			if (logger.isInfo()) {
				logger.info(this, "sub data to " + numberSize);
			}
			QueryModel queryModel = null;
			// 切分方式：精准切分
			if (precise) {
				querys = new ArrayList<Data>();
				// 获取分组数据
				List<Map<String, Object>> groupData = this.getGroupData();
				for(Map<String, Object> data : groupData) {
					String key = String.valueOf(data.get("key"));// 字段值
					Integer doc_count = (Integer) data.get("doc_count");// 数量
					queryModel = new QueryModel();
					// 设置返回字段
					queryModel.set_source(QueryModel.get_source(columns));
					// 存在外部查询语句时，做组合查询
					if(StringUtils.isNotBlank(query)) {
						List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
						listMap.add(Y9JsonUtil.readHashMap(query));
						listMap.add(Y9JsonUtil.readHashMap(QueryModel.get_query(splitPk, key, QueryModel.TERM)));
						queryModel.setQuery(QueryModel.get_boolMustQuery(listMap));
					}else {
						queryModel.setQuery(QueryModel.get_query(splitPk, key, QueryModel.TERM));
					}
					queryModel.setSize(doc_count);
					
					querys.add(new StringData(Y9JsonUtil.writeValueAsString(queryModel)));
				}
			}else {
				querys = new ArrayList<Data>();
				List<Map<String, Integer>> parts = this.splitDataIntoParts(count, numberSize);
				for(Map<String, Integer> map : parts) {
					queryModel = new QueryModel();
					// 设置返回字段
					queryModel.set_source(QueryModel.get_source(columns));
					// 设置查询语句
					if(StringUtils.isNotBlank(query)) {
						queryModel.setQuery(query);
					}
					// 设置排序
					queryModel.setSort("{\""+splitPk+"\":{\"order\":\"asc\"}}");
					// 设置分页参数
					queryModel.setFrom(map.get("from"));
					queryModel.setSize(map.get("size"));
					
					querys.add(new StringData(Y9JsonUtil.writeValueAsString(queryModel)));
				}
			}
			if (logger.isInfo()) {
				logger.info(this, "sub data end: " + querys.size());
			}
		}else {
			// 但数据量过大限制必须使用切分
			if(count > 10000) {
				throw new TransferException(CommonErrorCode.WAIT_TIME_EXCEED, "数据过大，建议使用切分模式");
			}
			logger.info(this, "no sub data");
			querys = new ArrayList<Data>();
			QueryModel queryModel = new QueryModel();
			// 设置返回字段
			queryModel.set_source(QueryModel.get_source(columns));
			// 设置查询语句
			if(StringUtils.isNotBlank(query)) {
				queryModel.setQuery(query);
			}
			queryModel.setSize(count);
			querys.add(new StringData(Y9JsonUtil.writeValueAsString(queryModel)));
		}
		return querys;
	}
	
	/**
	 * 获取切分数量
	 * @param count
	 * @param parts
	 * @return
	 */
	private List<Map<String, Integer>> splitDataIntoParts(int count, int parts) {  
        if (count <= 0) {  
        	throw new TransferException(CommonErrorCode.CONFIG_ERROR, "无数据，无法切分");  
        }  
        List<Map<String, Integer>> listMap = new ArrayList<Map<String,Integer>>();
        int chunkSize = count / parts; // 计算每份的平均大小  
        int remainder = count % parts; // 计算余数
        if(remainder > 0) {
		parts += 1;
        }
        for (int i = 1; i <= parts; i++) {
        	Map<String, Integer> result = new HashMap<String, Integer>();
		int start = (i - 1) * chunkSize;
            result.put("from", start);
            result.put("size", i == parts ? remainder : chunkSize);
            listMap.add(result);
        }  
        return listMap;  
    }
	
	/**
	 * 根据切分字段获取分组数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getGroupData() {
		QueryModel queryModel = new QueryModel();
		queryModel.setAggs("{\"aggs_name\":{\"terms\":{\"field\":\""+splitPk+"\", \"size\":100}}}");
		if(StringUtils.isNotBlank(query)) {
			queryModel.setQuery(query);
		}
		try {
			Map<String, Object> map = elasticsearchRestClient.search(queryModel, indexName);
			return (List<Map<String, Object>>) map.get("data");
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "elastic分组查询-执行报错", e);
		}
	}

}
