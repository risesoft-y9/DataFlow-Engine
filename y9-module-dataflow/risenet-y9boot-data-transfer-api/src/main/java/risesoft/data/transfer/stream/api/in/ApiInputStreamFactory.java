package risesoft.data.transfer.stream.api.in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.risesoft.model.RequestModel;
import net.risesoft.y9.json.Y9JsonUtil;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.data.StringData;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.stream.in.DataInputStream;
import risesoft.data.transfer.core.stream.in.DataInputStreamFactory;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ValueUtils;

/**
 * 接口数据源-输入流工厂
 * @author pzx
 *
 */
public class ApiInputStreamFactory implements DataInputStreamFactory {

	private Logger logger;

	private RequestModel requestModel;// 请求参数
	private List<String> columns;// 返回字段列表
	private Map<String, String> columnTypes;// 字段类型
	private Boolean isPage;// 是否分页接口
	private Integer totalPages;// 总页数
	private Integer page;// 初始页数

	@SuppressWarnings("unchecked")
	public ApiInputStreamFactory(Configuration configuration, LoggerFactory loggerFactory) {
		this.requestModel = Y9JsonUtil.readValue(configuration.getString("requestModel"), RequestModel.class);
		this.columns = ValueUtils.getRequired(configuration.getList("column", String.class), "缺失返回字段列表");
		this.columnTypes = configuration.get("columnTypes", Map.class);
		this.isPage = configuration.getBool("isPage", false);
		this.totalPages = configuration.getInt("totalPages");
		this.page = configuration.getInt("page", 1);
		logger = loggerFactory.getLogger(configuration.getString("name", "ApiInputStreamFactory"));
	}

	@Override
	public DataInputStream getStream() {
		return new ApiInputStream(columns, columnTypes, isPage, logger);
	}

	@Override
	public void init() {
		
	}

	@Override
	public void close() throws Exception {

	}

	@Override
	public List<Data> splitToData(int executorSize) throws Exception {
		List<Data> querys = null;
		// 判断是否分页
		if (isPage) {
			if(totalPages == 0) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "接口返回数据总量为0");
			}
			querys = new ArrayList<Data>();
			// 遍历设置分页参数
			List<Map<String, Object>> params = requestModel.getParams();
			while(page < totalPages) {
				List<Map<String, Object>> newParams = copyList(params);
				newParams.stream().map((item) -> {
					String value = item.get("value") + "";
		        	if(value.startsWith("$page{")) {
		        		item.put("value", page);
		        	}
		            return item;
		        }).collect(Collectors.toList());
				page++;
				
				requestModel.setParams(newParams);
				querys.add(new StringData(Y9JsonUtil.writeValueAsString(requestModel)));
			}
		}else {
			querys = new ArrayList<Data>();
			querys.add(new StringData(Y9JsonUtil.writeValueAsString(requestModel)));
		}
		return querys;
	}
	
	private List<Map<String, Object>> copyList(List<Map<String, Object>> listMap) {
		List<Map<String, Object>> newList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> map : listMap) {
			Map<String, Object> newMap = new HashMap<String, Object>();
			newMap.putAll(map);
			newList.add(newMap);
		}
		return newList;
	}

}
