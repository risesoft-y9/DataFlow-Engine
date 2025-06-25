package risesoft.data.transfer.stream.es.out;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.risesoft.elastic.client.ElasticsearchRestClient;
import net.risesoft.y9.json.Y9JsonUtil;
import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.record.Ack;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.stream.out.DataOutputStream;

public class ElasticsearchOutputStream implements DataOutputStream {
	
	private ElasticsearchRestClient elasticsearchRestClient; 
	private String indexName;// 索引表
	private List<String> columns;// 字段列表
	private String writerType;// 输入类型
	private Logger logger;

	public ElasticsearchOutputStream(ElasticsearchRestClient elasticsearchRestClient, String indexName, List<String> columns,
			String writerType, Logger logger) {
		this.elasticsearchRestClient = elasticsearchRestClient;
		this.indexName = indexName;
		this.columns = columns;
		this.writerType = writerType;
		this.logger = logger;
	}

	@Override
	public void close() throws Exception {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writer(List<Record> records, Ack ack) {
		try {
			logger.debug(this, "plan writer: " + records.size());
			String document = "";
			// 存储记录
			Map<String, Record> dataMap = new HashMap<String, Record>();
			// 遍历数据
			for (Record record : records) {
				try {
					String id = "";
					Map<String, Object> data = new HashMap<String, Object>();
					//获取字段数据
					Map<String, Column> recordMap = getRecordMap(record);
					//遍历字段
					for(String name : columns) {
						Column column = recordMap.get(name);
						if(column != null) {
							if(name.equals("id")) {
								id = String.valueOf(column.getRawData());
							}
							data.put(name, column.getRawData());
						}
					}
					dataMap.put(id, record);// 记录
					// 插入一条数据
					//elasticsearchRestClient.addDocument(indexName, Y9JsonUtil.writeValueAsString(data), id);
					//ack.confirm(record);
					
					// 拼接数据进行批量插入
					document += "{\"index\":{\"_id\":\""+id+"\"}}\n";
					document += Y9JsonUtil.writeValueAsString(data) + "\n";
				} catch (Exception e) {
					logger.error(this, record + " error:" + e.getMessage());
					ack.cancel(record, e, e.toString());
				}
			}
			// 批量插入
			String response = elasticsearchRestClient.bulkAddDocument(indexName, document);
			// 解析返回结果
			Map<String, Object> map = Y9JsonUtil.readHashMap(response);
			// 检查是否有错误信息，有遍历处理
			boolean errors = (boolean) map.get("errors");
			if(errors) {
				List<Map<String, Object>> items = (List<Map<String, Object>>) map.get("items");
				for(Map<String, Object> item : items) {
					Map<String, Object> info = (Map<String, Object>) item.get("index");
					if(info != null) {
						String id = (String) info.get("_id");
						int status = (int) info.get("status");
						if(status == 200 || status == 201) {
							ack.confirm(dataMap.get(id));
						} else {
							// 获取报错信息
							Map<String, Object> errorMsg = (Map<String, Object>) info.get("error");
							ack.cancel(dataMap.get(id), new Throwable((String) errorMsg.get("type")), (String) errorMsg.get("reason"));
						}
					}
				}
			} else {
				ack.confirm(records);
			}
			
			if (logger.isDebug()) {
				logger.debug(this, "confirm end: " + records.size());
			}
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "执行插入报错", e);
		}
	}
	
	/**
	 * 获取字段数据
	 * @param record
	 * @return
	 */
	private Map<String, Column> getRecordMap(Record record) {
		Map<String, Column> recordMap = new HashMap<String, Column>();
		for (int i = 0; i < record.getColumnNumber(); i++) {
			recordMap.put(record.getColumn(i).getName(), record.getColumn(i));
		}
		return recordMap;
	}

	@Override
	public void writer(Record record, Ack ack) {
		try {
			String id = "";
			Map<String, Object> data = new HashMap<String, Object>();
			//获取字段数据
			Map<String, Column> recordMap = getRecordMap(record);
			//遍历字段
			for(String name : columns) {
				Column column = recordMap.get(name);
				if(column != null) {
					if(name.equals("id")) {
						id = String.valueOf(column.getRawData());
					}
					data.put(name, column.getRawData());
				}
			}
			// 插入一条数据
			elasticsearchRestClient.addDocument(indexName, Y9JsonUtil.writeValueAsString(data), id);
			ack.confirm(record);
		} catch (Exception e) {
			e.printStackTrace();
			ack.cancel(record, e, e.getMessage());
		}
	}

}
