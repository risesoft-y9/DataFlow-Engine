package risesoft.data.transfer.stream.es.in;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.risesoft.elastic.client.ElasticsearchRestClient;
import net.risesoft.elastic.client.pojo.QueryModel;
import net.risesoft.y9.json.Y9JsonUtil;
import risesoft.data.transfer.core.channel.InChannel;
import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.BoolColumn;
import risesoft.data.transfer.core.column.impl.DateColumn;
import risesoft.data.transfer.core.column.impl.DoubleColumn;
import risesoft.data.transfer.core.column.impl.LongColumn;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.data.StringData;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.record.DefaultRecord;
import risesoft.data.transfer.core.stream.in.DataInputStream;

/**
 * elastic输入流
 * @author pzx
 *
 */
public class ElasticsearchInputStream implements DataInputStream {
	
	private ElasticsearchRestClient elasticsearchRestClient;

	private String indexName;// 索引名称
	
	private Map<String, String> columnTypes;// 字段类型
	
	private Logger logger;

	public ElasticsearchInputStream(ElasticsearchRestClient elasticsearchRestClient, String indexName, Map<String, String> columnTypes, Logger logger) {
		super();
		this.elasticsearchRestClient = elasticsearchRestClient;
		this.indexName = indexName;
		this.columnTypes = columnTypes;
		this.logger = logger;
	}

	@Override
	public void close() throws Exception {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(Data data, InChannel inChannel) {
		Map<String, Object> dataMap = null;
		QueryModel queryModel = Y9JsonUtil.readValue(((StringData) data).getValue(), QueryModel.class);
		try {
			dataMap = elasticsearchRestClient.search(queryModel, indexName);
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "elastic查询-执行报错", e);
		}
		try {
			logger.debug(this, "readData start");
			// 获取数据列表
			List<Map<String, Object>> listMap = (List<Map<String, Object>>) dataMap.get("data");
			for(Map<String, Object> map : listMap) {
				// 写入数据列
				DefaultRecord record = new DefaultRecord();
				try {
					// 遍历列数据
					for (Map.Entry<String, Object> entry : map.entrySet()) {  
						String column_name = entry.getKey();// 获得字段名
						String type = columnTypes.get(column_name);
						record.addColumn(getColumn(type, String.valueOf(entry.getValue()), column_name));
					}
					inChannel.writer(record);
				} catch (Exception e) {
					inChannel.collectDirtyRecord(record, e, "脏数据" + e.getMessage());
				}
			}
			inChannel.flush();
			logger.debug(this, "readData end");
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "读取数据报错", e);
		}
	}
	
	private Column getColumn(String type, String data, String name) {
		switch (type) {
			case "keyword":
			case "text":
				return new StringColumn(data, name);
			case "long":
			case "integer":
			case "short":
				return new LongColumn(data, name);
			case "double":
			case "float":
				return new DoubleColumn(data, name);
			case "boolean":
				return new BoolColumn(data, name);
			case "date":
				if(data.indexOf("T") > -1) {
					return new StringColumn(data, name);
				}
				return new DateColumn(getDate(data), name);
			default:
				return new StringColumn(data, name);
		}
	}
	
	private Date getDate(String data) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(data.indexOf(":") > -1) {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		try {
			return sdf.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
