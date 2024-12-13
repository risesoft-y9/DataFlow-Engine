package net.risesoft.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.persistence.config.ConfigService;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.RequestModel;
import net.risesoft.pojo.DifferentField;
import net.risesoft.pojo.TaskConfigModel;
import net.risesoft.util.ApiTest;
import net.risesoft.util.DataServiceUtil;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataInterfaceEntity;
import net.risesoft.y9public.entity.DataInterfaceParamsEntity;
import net.risesoft.y9public.entity.DataMappingArgsEntity;
import net.risesoft.y9public.entity.DataSourceEntity;
import net.risesoft.y9public.entity.DataTable;
import net.risesoft.y9public.entity.DataTableField;
import net.risesoft.y9public.entity.DataTaskCoreEntity;
import net.risesoft.y9public.entity.DataTaskMakeUpEntity;
import net.risesoft.y9public.repository.DataInterfaceParamsRepository;
import net.risesoft.y9public.repository.DataInterfaceRepository;
import net.risesoft.y9public.repository.DataMappingArgsRepository;
import net.risesoft.y9public.repository.DataMappingRepository;
import net.risesoft.y9public.repository.DataSourceRepository;
import net.risesoft.y9public.repository.DataTableFieldRepository;
import net.risesoft.y9public.repository.DataTableRepository;
import net.risesoft.y9public.repository.DataTaskCoreRepository;
import net.risesoft.y9public.repository.DataTaskMakeUpRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class TaskMakeUpListener {
	
	private final DataTaskCoreRepository dataTaskCoreRepository;
	private final DataTaskMakeUpRepository dataTaskMakeUpRepository;
	private final DataSourceRepository dataSourceRepository;
	private final DataTableRepository dataTableRepository;
	private final DataTableFieldRepository dataTableFieldRepository;
	private final DataMappingRepository dataMappingRepository;
	private final DataMappingArgsRepository dataMappingArgsRepository;
	private final DataInterfaceParamsRepository dataInterfaceParamsRepository;
	private final DataInterfaceRepository dataInterfaceRepository;
	private final ConfigService configService;
    
    @SuppressWarnings("unchecked")
	@Async
    public void onTaskMakeUp(String taskId, String taskName, TaskConfigModel configModel) {
    	// 先删除旧数据
    	List<DataTaskMakeUpEntity> taskMakeUpList = dataTaskMakeUpRepository.findByTaskId(taskId);
    	if(taskMakeUpList != null && taskMakeUpList.size() > 0) {
    		dataTaskMakeUpRepository.deleteAll(taskMakeUpList);
    	}
    	
    	List<CompletableFuture<Void>> futures = new ArrayList<>();
    	
    	// 保存源头数据信息
    	futures.add(CompletableFuture.runAsync(() -> saveSource(taskId, configModel)));
    	
    	// 保存目标数据信息
    	futures.add(CompletableFuture.runAsync(() -> saveTarget(taskId, configModel)));
    	
    	// 保存输出通道
    	futures.add(CompletableFuture.runAsync(() -> {
    		DataTaskCoreEntity channelOut = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.CHANNEL,
        			DataServiceUtil.OUTPUT, "name", 1);
    		saveCore(taskId, DataServiceUtil.CHANNEL + "." + DataServiceUtil.OUTPUT, channelOut, 1);
    	}));
    	
    	// 保存输入通道
    	futures.add(CompletableFuture.runAsync(() -> {
    		DataTaskCoreEntity channelIn = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.CHANNEL,
        			DataServiceUtil.INPUT, "name", 1);
    		saveCore(taskId, DataServiceUtil.CHANNEL + "." + DataServiceUtil.INPUT, channelIn, 1);
    	}));
    	
    	// 保存交换机
    	futures.add(CompletableFuture.runAsync(() -> {
    		DataTaskCoreEntity exchange = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.EXCHANGE,
        			DataServiceUtil.EXCHANGE, "name", 1);
    		saveCore(taskId, DataServiceUtil.EXCHANGE, exchange, 1);
    	}));
    	
    	// 保存执行器
    	futures.add(CompletableFuture.runAsync(() -> {
    		DataTaskCoreEntity executorInput = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.EXECUTOR,
        			DataServiceUtil.INPUT, "name", 1);
    		saveCore(taskId, DataServiceUtil.EXECUTOR + "." + DataServiceUtil.INPUT, executorInput, 1);
    	}));
    	futures.add(CompletableFuture.runAsync(() -> {
    		DataTaskCoreEntity executorOut = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.EXECUTOR,
        			DataServiceUtil.OUTPUT, "name", 1);
    		saveCore(taskId, DataServiceUtil.EXECUTOR + "." + DataServiceUtil.OUTPUT, executorOut, 1);
    	}));
    	
    	// 保存其它配置
    	int index = 1;
    	List<DataTaskCoreEntity> printLogs = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyName(taskId, DataServiceUtil.PLUGS,
    			DataServiceUtil.PRINTLOG, "name");
    	if(printLogs != null && printLogs.size() > 0) {
    		for(DataTaskCoreEntity dataTaskCoreEntity : printLogs) {
    			int num = index;
    			futures.add(CompletableFuture.runAsync(() -> saveCore(taskId, DataServiceUtil.PLUGS, dataTaskCoreEntity, num)));
    			index++;
    		}
    	}
    	
    	List<DataTaskCoreEntity> dirtyDatas = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyName(taskId, DataServiceUtil.PLUGS,
    			DataServiceUtil.DIRTYDATA, "name");
    	if(dirtyDatas != null && dirtyDatas.size() > 0) {
    		for(DataTaskCoreEntity dataTaskCoreEntity : dirtyDatas) {
    			int num = index;
    			futures.add(CompletableFuture.runAsync(() -> saveCore(taskId, DataServiceUtil.PLUGS, dataTaskCoreEntity, num)));
    			index++;
    		}
    	}
    	
    	// 等待所有任务执行完成再刷新
    	futures.forEach(CompletableFuture::join);
    	
    	Map<String, Object> dmap = new HashMap<String, Object>();
    	// 保存异字段配置
    	DataTaskCoreEntity different = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.PLUGS,
    			DataServiceUtil.DIFFERENT, "field", 1);
    	if(different != null) {
        	List<DifferentField> differentFields = Y9JsonUtil.readList(different.getValue(), DifferentField.class);
        	for(DifferentField field : differentFields) {
        		String sName = "", tName = "";
        		if(configModel.getSourceType().equals("api")) {
        			sName = dataInterfaceParamsRepository.findById(field.getSource()).orElse(null).getParamName();
        		}else {
        			sName = dataTableFieldRepository.findById(field.getSource()).orElse(null).getName();
        		}
        		if(configModel.getTargetType().equals("api")) {
        			tName = dataInterfaceParamsRepository.findById(field.getTarget()).orElse(null).getParamName();
        		}else {
        			tName = dataTableFieldRepository.findById(field.getTarget()).orElse(null).getName();
        		}
        		dmap.put(sName, tName);
        	}
    	}
    	
    	//检查大小写
    	if(!configModel.getSourceType().equals("ftp")) {
    		DataTaskMakeUpEntity makeUpEntity1 = dataTaskMakeUpRepository.findByTaskIdAndTypeNameAndTabIndex(taskId, "job.input", 1);
        	if(makeUpEntity1 != null) {
        		Map<String, Object> sourceMap = Y9JsonUtil.readHashMap(makeUpEntity1.getArgsValue());
        		List<String> sourceColumns = (List<String>) sourceMap.get("column");
        		
        		DataTaskMakeUpEntity makeUpEntity2 = dataTaskMakeUpRepository.findByTaskIdAndTypeNameAndTabIndex(taskId, "job.output", 1);
        		Map<String, Object> targetMap = Y9JsonUtil.readHashMap(makeUpEntity2.getArgsValue());
        		List<String> targetColumns = (List<String>) targetMap.get("column");
        		
                // 忽略大小写筛选相同元素
                for(String source : sourceColumns) {
                	for(String target : targetColumns) {
                		if(source.equalsIgnoreCase(target) && !source.equals(target)) {
                			dmap.put(source, target);
                			continue;
                		}
                	}
                }
        	}
    	}
    	
    	if(!dmap.isEmpty()) {
    		DataTaskMakeUpEntity dataTaskMakeUpEntity = new DataTaskMakeUpEntity();
        	dataTaskMakeUpEntity.setId(Y9IdGenerator.genId());
        	dataTaskMakeUpEntity.setTaskId(taskId);
        	dataTaskMakeUpEntity.setTypeName(DataServiceUtil.PLUGS);
        	dataTaskMakeUpEntity.setNameValue(DataServiceUtil.DIFFERENTCLASS);
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("renameMap", dmap);
        	dataTaskMakeUpEntity.setArgsValue(Y9JsonUtil.writeValueAsString(map));
        	dataTaskMakeUpEntity.setTabIndex(index);
        	dataTaskMakeUpRepository.save(dataTaskMakeUpEntity);
        	index++;
    	}
    	
    	configService.refreshConfig(taskId, taskName);
    	
        LOGGER.debug("任务["+taskId+"]-配置组成更新完成");
    }
    
    private void saveCore(String taskId, String typeName, DataTaskCoreEntity dataTaskCoreEntity, Integer tabIndex) {
    	if(dataTaskCoreEntity != null) {
    		DataTaskMakeUpEntity dataTaskMakeUpEntity = new DataTaskMakeUpEntity();
        	dataTaskMakeUpEntity.setId(Y9IdGenerator.genId());
        	dataTaskMakeUpEntity.setTaskId(taskId);
        	dataTaskMakeUpEntity.setTypeName(typeName);
        	dataTaskMakeUpEntity.setNameValue(dataTaskCoreEntity.getValue());
        	
        	List<DataTaskCoreEntity> taskCoreList = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndSequence(taskId, 
        			dataTaskCoreEntity.getTypeName(), dataTaskCoreEntity.getDataType(), tabIndex);
        	Map<String, Object> map = new HashMap<String, Object>();
        	List<String> upNameList = new ArrayList<String>();
        	for(DataTaskCoreEntity taskCore : taskCoreList) {
        		if(!taskCore.getKeyName().equals("name")) {
        			DataMappingArgsEntity dataMappingArgsEntity = dataMappingArgsRepository.findById(taskCore.getArgsId()).orElse(null);
        			// 计算二级参数情况
        			if(dataMappingArgsEntity != null && dataMappingArgsEntity.getDataType() == 2) {
        				if(!upNameList.contains(dataMappingArgsEntity.getUpName())) {
        					Map<String, Object> rmap = new HashMap<String, Object>();
            				// 获取同二级参数列表
            				List<DataMappingArgsEntity> dataMappingArgsList = dataMappingArgsRepository.
            						findByUpNameAndMappingId(dataMappingArgsEntity.getUpName(), dataMappingArgsEntity.getMappingId());
            				for(DataMappingArgsEntity mappingArgs : dataMappingArgsList) {
            					DataTaskCoreEntity info = dataTaskCoreRepository.findByTaskIdAndArgsIdAndSequence(taskId, mappingArgs.getId(), tabIndex);
            					if(info != null) {
            						rmap.put(mappingArgs.getName(), info.getValue());
            					}
            				}
            				map.put(dataMappingArgsEntity.getUpName(), rmap);
            				upNameList.add(dataMappingArgsEntity.getUpName());// 存储，避免重复计算
        				}
        			}else {
        				map.put(taskCore.getKeyName(), taskCore.getValue());
        			}
        		}
        	}
        	dataTaskMakeUpEntity.setArgsValue(Y9JsonUtil.writeValueAsString(map));
        	dataTaskMakeUpEntity.setTabIndex(tabIndex);
        	dataTaskMakeUpRepository.save(dataTaskMakeUpEntity);
    	}
    }
    
    private void saveSource(String taskId, TaskConfigModel configModel) {
    	DataTaskMakeUpEntity dataTaskMakeUpEntity = new DataTaskMakeUpEntity();
    	dataTaskMakeUpEntity.setId(Y9IdGenerator.genId());
    	dataTaskMakeUpEntity.setTaskId(taskId);
    	dataTaskMakeUpEntity.setTypeName("job.input");
    	// 执行类
    	dataTaskMakeUpEntity.setNameValue(dataMappingRepository.findById(configModel.getSourceName()).orElse(null).getClassName());
    	Map<String, Object> map = new HashMap<String, Object>();
    	// 获取源头数据源信息
    	if(configModel.getSourceType().equals("api")) {
    		map.put("requestModel", configModel.getWhereSql());
    		List<String> sourceCloumn = new ArrayList<String>();// 接口返回字段
    		Map<String, String> columnTypes = new HashMap<String, String>();// 字段类型
    		// 获取接口返回参数
    		List<DataInterfaceParamsEntity> paramList = dataInterfaceParamsRepository.findByParentIdAndDataType(configModel.getSourceTable(), 1);
    		for(DataInterfaceParamsEntity param : paramList) {
    			sourceCloumn.add(param.getParamName());
    			columnTypes.put(param.getParamName(), param.getParamType());
    		}
    		map.put("column", sourceCloumn);
    		map.put("columnTypes", columnTypes);
    		// 判断是否有分页参数
    		if(configModel.getWhereSql().indexOf("$page{") > -1) {
    			RequestModel requestModel = Y9JsonUtil.readValue(configModel.getWhereSql(), RequestModel.class);
    			map.put("isPage", true);
    			int page = 0;
    			List<Map<String, Object>> params = requestModel.getParams();
    			for(Map<String, Object> pmap : params) {
    				String value = pmap.get("value") + "";
		        	if(value.startsWith("$page{")) {
		        		page = Integer.valueOf(value.replace("$page{", "").replace("}", ""));
		        		pmap.put("value", page);
		        	}
    			}
    			map.put("page", page);
    			requestModel.setParams(params);
    			// 获取总页数
    			Map<String, Object> resMap = Y9JsonUtil.readHashMap(ApiTest.sendApi(requestModel));
    			if((boolean) resMap.get("success")) {
    				map.put("totalPages", resMap.get("totalPages"));
    			}else {
    				map.put("totalPages", 0);
    			}
    		}else {
    			map.put("isPage", false);
    		}
    	}else if(configModel.getSourceType().equals("ftp")) {
    		DataSourceEntity dataSourceEntity = dataSourceRepository.findById(configModel.getSourceId()).orElse(null);
    		String[] url = dataSourceEntity.getUrl().split(":");
    		map.put("host", url[0]);
    		map.put("userName", dataSourceEntity.getUsername());
        	map.put("password", dataSourceEntity.getPassword());
        	map.put("port", url[1]);
        	map.put("path", configModel.getSourceTable());
        	map.put("encoding", DataServiceUtil.getEncoding(configModel.getFetchSize()));
        	map.put("fileName", configModel.getWhereSql());
        	map.put("date", configModel.getSplitPk());
    	}else {
    		DataSourceEntity dataSourceEntity = dataSourceRepository.findById(configModel.getSourceId()).orElse(null);
        	map.put("jdbcUrl", dataSourceEntity.getUrl());
        	map.put("userName", dataSourceEntity.getUsername());
        	map.put("password", dataSourceEntity.getPassword());
        	// 获取源头表信息
        	DataTable dataTable = dataTableRepository.findById(configModel.getSourceTable()).orElse(null);
        	map.put("tableName", dataTable.getName());
        	
        	if(StringUtils.isNotBlank(configModel.getSplitPk())) {
        		DataTableField dataTableField = dataTableFieldRepository.findById(configModel.getSplitPk()).orElse(null);
            	map.put("splitPk", dataTableField.getName());
        	}else {
        		map.put("splitPk", "");
        	}
        	
        	map.put("where", configModel.getWhereSql());
        	map.put("precise", configModel.getPrecise());
        	map.put("tableNumber", configModel.getTableNumber());
        	map.put("fetchSize", configModel.getFetchSize());
        	map.put("splitFactor", configModel.getSplitFactor());
        	
        	// 获取源头字段信息
        	String[] sourceCloumns = configModel.getSourceCloumn().split(",");
        	List<String> sourceCloumn = new ArrayList<String>();
    		for(String field : sourceCloumns) {
    			DataTableField tableField = dataTableFieldRepository.findById(field).orElse(null);
    			if(tableField != null) {
    				sourceCloumn.add(tableField.getName());
    			}
    		}
        	map.put("column", sourceCloumn);
    	}
    	dataTaskMakeUpEntity.setArgsValue(Y9JsonUtil.writeValueAsString(map));
    	dataTaskMakeUpEntity.setTabIndex(1);
    	dataTaskMakeUpRepository.save(dataTaskMakeUpEntity);
    }
    
    private void saveTarget(String taskId, TaskConfigModel configModel) {
    	DataTaskMakeUpEntity dataTaskMakeUpEntity = new DataTaskMakeUpEntity();
    	dataTaskMakeUpEntity.setId(Y9IdGenerator.genId());
    	dataTaskMakeUpEntity.setTaskId(taskId);
    	dataTaskMakeUpEntity.setTypeName("job.output");
    	// 执行类
    	dataTaskMakeUpEntity.setNameValue(dataMappingRepository.findById(configModel.getTargeName()).orElse(null).getClassName());
    	Map<String, Object> map = new HashMap<String, Object>();
    	// 获取目标数据源信息
    	if(configModel.getTargetType().equals("api")) {
    		// 获取接口信息
    		DataInterfaceEntity dataInterfaceEntity = dataInterfaceRepository.findById(configModel.getTargetTable()).orElse(null);
    		RequestModel requestModel = new RequestModel();
    		requestModel.setUrl(dataInterfaceEntity.getInterfaceUrl());
    		requestModel.setMethod(dataInterfaceEntity.getRequestType());
    		requestModel.setContentType(dataInterfaceEntity.getContentType());
    		List<String> column = new ArrayList<String>();// 接口返回字段
    		List<Map<String, Object>> headers = new ArrayList<Map<String,Object>>();
    		List<Map<String, Object>> params = new ArrayList<Map<String,Object>>();
    		Map<String, Object> body = new HashMap<String, Object>();
    		boolean isBody = false;
    		// 获取接口请求参数
    		List<DataInterfaceParamsEntity> paramList = dataInterfaceParamsRepository.findByParentIdAndDataType(configModel.getTargetTable(), 0);
    		for(DataInterfaceParamsEntity param : paramList) {
    			if(param.getReqType().equals("Params") && StringUtils.isNotBlank(param.getParamValue())) {
    				Map<String, Object> pMap = new HashMap<String, Object>();
    				pMap.put("name", param.getParamName());
    				pMap.put("value", param.getParamValue());
    				params.add(pMap);
    			}
    			if(param.getReqType().equals("Headers") && StringUtils.isNotBlank(param.getParamValue())) {
    				Map<String, Object> hMap = new HashMap<String, Object>();
    				hMap.put("name", param.getParamName());
    				hMap.put("value", param.getParamValue());
    				headers.add(hMap);
    			}
    			if(param.getReqType().equals("Body")) {
    				isBody = true;
    				if(StringUtils.isNotBlank(param.getParamValue())) {
    					body.put(param.getParamName(), param.getParamValue());
    				}
    			}
    			if(!param.getReqType().equals("Headers")) {
    				column.add(param.getParamName());
    			}
    		}
    		if(body!= null && !body.isEmpty()) {
    			requestModel.setBody(Y9JsonUtil.writeValueAsString(body));
    		}
    		map.put("isBody", isBody);
    		requestModel.setHeaders(headers);
    		requestModel.setParams(params);
    		map.put("requestModel", Y9JsonUtil.writeValueAsString(requestModel));
    		map.put("column", column);
    	}else if(configModel.getTargetType().equals("ftp")) {
    		map.put("path", configModel.getTargetTable());
    		map.put("bufferSize", configModel.getTableNumber());
    	}else {
    		DataSourceEntity dataSourceEntity = dataSourceRepository.findById(configModel.getTargetId()).orElse(null);
        	map.put("jdbcUrl", dataSourceEntity.getUrl());
        	map.put("userName", dataSourceEntity.getUsername());
        	map.put("password", dataSourceEntity.getPassword());
        	// 获取目标表信息
        	DataTable dataTable = dataTableRepository.findById(configModel.getTargetTable()).orElse(null);
        	map.put("tableName", dataTable.getName());
        	
        	if(configModel.getWriterType().equals("update") || configModel.getWriterType().equals("replace")) {
        		String text = "";
        		String[] ids = configModel.getUpdateField().split(",");
    			for(String field : ids) {
    				DataTableField tableField = dataTableFieldRepository.findById(field).orElse(null);
    				if(tableField != null) {
    					text += StringUtils.isBlank(text)?tableField.getName():","+tableField.getName();
    				}
    			}
        		map.put("writerType", configModel.getWriterType() + "(" + text + ")");
        	}else {
        		map.put("writerType", configModel.getWriterType());
        	}
        	
        	// 获取目标字段信息
        	String[] targetCloumns = configModel.getTargetCloumn().split(",");
        	List<String> targetCloumn = new ArrayList<String>();
    		for(String field : targetCloumns) {
    			DataTableField tableField = dataTableFieldRepository.findById(field).orElse(null);
    			if(tableField != null) {
    				targetCloumn.add(tableField.getName());
    			}
    		}
        	map.put("column", targetCloumn);
    	}
    	dataTaskMakeUpEntity.setArgsValue(Y9JsonUtil.writeValueAsString(map));
    	dataTaskMakeUpEntity.setTabIndex(1);
    	dataTaskMakeUpRepository.save(dataTaskMakeUpEntity);
    }

}
