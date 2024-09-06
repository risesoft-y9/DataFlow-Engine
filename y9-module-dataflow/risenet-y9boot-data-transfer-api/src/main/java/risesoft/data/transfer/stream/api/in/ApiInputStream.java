package risesoft.data.transfer.stream.api.in;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.risesoft.model.RequestModel;
import net.risesoft.util.ApiTest;
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
 * 接口数据源-输入流
 * @author pzx
 *
 */
public class ApiInputStream implements DataInputStream {
	
	private List<String> columns;// 返回字段列表
	private Map<String, String> columnTypes;// 字段类型
	private boolean isPage;// 是否分页，分页和非分页返回的结构不一样
	
	private Logger logger;

	public ApiInputStream(List<String> columns, Map<String, String> columnTypes, boolean isPage, Logger logger) {
		super();
		this.columns = columns;
		this.columnTypes = columnTypes;
		this.isPage = isPage;
		this.logger = logger;
	}

	@Override
	public void close() throws Exception {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(Data data, InChannel inChannel) {
		Map<String, Object> dataMap = null;
		RequestModel requestModel = Y9JsonUtil.readValue(((StringData) data).getValue(), RequestModel.class);
		try {
			dataMap = Y9JsonUtil.readHashMap(ApiTest.sendApi(requestModel));
			if(dataMap == null || !(boolean) dataMap.get("success")) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "接口返回失败，检查接口");
			}
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "接口调用报错", e);
		}
		try {
			logger.debug(this, "readData start");
			// 获取数据列表
			List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
			if(isPage) {
				listMap = (List<Map<String, Object>>) dataMap.get("rows");
			}else {
				listMap = (List<Map<String, Object>>) dataMap.get("data");
			}
			for(Map<String, Object> map : listMap) {
				// 写入数据列
				DefaultRecord record = new DefaultRecord();
				try {
					// 遍历列数据
					for(String column : columns) {
						String type = columnTypes.get(column);// 获取字段类型
						record.addColumn(getColumn(type, String.valueOf(map.get(column)), column));
					}
					inChannel.writer(record);
				} catch (Exception e) {
					inChannel.collectDirtyRecord(record, e, "脏数据-" + e.getMessage());
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
