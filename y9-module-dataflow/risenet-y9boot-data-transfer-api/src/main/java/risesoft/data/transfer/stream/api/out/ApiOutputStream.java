package risesoft.data.transfer.stream.api.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.risesoft.model.RequestModel;
import net.risesoft.util.ApiTest;
import net.risesoft.y9.json.Y9JsonUtil;
import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.record.Ack;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.stream.out.DataOutputStream;

/**
 * 接口数据源-输出流
 * @author pzx
 *
 */
public class ApiOutputStream implements DataOutputStream {
	
	private RequestModel requestModel;// 请求参数
	private List<String> columns;// 字段列表
	private Boolean isBody;//是否body参数
	private Logger logger;

	public ApiOutputStream(RequestModel requestModel, List<String> columns, Boolean isBody, Logger logger) {
		this.requestModel = requestModel;
		this.columns = columns;
		this.isBody = isBody;
		this.logger = logger;
	}

	@Override
	public void close() throws Exception {
		
	}

	@Override
	public void writer(List<Record> records, Ack ack) {
		try {
			logger.debug(this, "plan writer: " + records.size());
			// 遍历数据
			for (Record record : records) {
				try {
					//获取字段数据
					Map<String, Column> recordMap = getRecordMap(record);
					if(isBody) {
						Map<String, Object> data = new HashMap<String, Object>();
						//遍历字段
						for(String name : columns) {
							Column column = recordMap.get(name);
							if(column != null) {
								data.put(name, column.getRawData());
							}
						}
						// 插入一条数据
						requestModel.setBody(Y9JsonUtil.writeValueAsString(data));
					}else {
						List<Map<String, Object>> params = new ArrayList<Map<String,Object>>();
						//遍历字段
						for(String name : columns) {
							Column column = recordMap.get(name);
							if(column != null) {
								Map<String, Object> data = new HashMap<String, Object>();
								data.put("name", name);
								data.put("value", column.getRawData());
								params.add(data);
							}
						}
						// 插入一条数据
						requestModel.setParams(params);
					}
					Map<String, Object> dataMap = Y9JsonUtil.readHashMap(ApiTest.sendApi(requestModel));
					if(!(boolean) dataMap.get("success")) {
						throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "执行插入失败，接口返回false");
					}
					ack.confirm(record);
				} catch (Exception e) {
					logger.error(this, record + " error:" + e.getMessage());
					ack.cancel(record, e, e.toString());
				}
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
			//获取字段数据
			Map<String, Column> recordMap = getRecordMap(record);
			if(isBody) {
				Map<String, Object> data = new HashMap<String, Object>();
				//遍历字段
				for(String name : columns) {
					Column column = recordMap.get(name);
					if(column != null) {
						data.put(name, column.getRawData());
					}
				}
				// 插入一条数据
				requestModel.setBody(Y9JsonUtil.writeValueAsString(data));
			}else {
				List<Map<String, Object>> params = new ArrayList<Map<String,Object>>();
				//遍历字段
				for(String name : columns) {
					Column column = recordMap.get(name);
					if(column != null) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("name", name);
						data.put("value", column.getRawData());
						params.add(data);
					}
				}
				// 插入一条数据
				requestModel.setParams(params);
			}
			Map<String, Object> dataMap = Y9JsonUtil.readHashMap(ApiTest.sendApi(requestModel));
			if(!(boolean) dataMap.get("success")) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "执行插入失败，接口返回false");
			}
			ack.confirm(record);
		} catch (Exception e) {
			e.printStackTrace();
			ack.cancel(record, e, e.getMessage());
		}
	}

}
