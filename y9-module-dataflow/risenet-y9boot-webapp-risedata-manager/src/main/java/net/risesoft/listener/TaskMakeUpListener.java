package net.risesoft.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.persistence.config.ConfigService;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.DifferentField;
import net.risesoft.pojo.TaskConfigModel;
import net.risesoft.util.DataServiceUtil;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataMappingArgsEntity;
import net.risesoft.y9public.entity.DataSourceEntity;
import net.risesoft.y9public.entity.DataTable;
import net.risesoft.y9public.entity.DataTableField;
import net.risesoft.y9public.entity.DataTaskCoreEntity;
import net.risesoft.y9public.entity.DataTaskMakeUpEntity;
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
	private final ConfigService configService;
    
    @Async
    public void onTaskMakeUp(String taskId, String taskName, TaskConfigModel configModel) {
    	// 先删除旧数据
    	List<DataTaskMakeUpEntity> taskMakeUpList = dataTaskMakeUpRepository.findByTaskId(taskId);
    	if(taskMakeUpList != null && taskMakeUpList.size() > 0) {
    		dataTaskMakeUpRepository.deleteAll(taskMakeUpList);
    	}
    	
    	// 保存源头数据信息
    	this.saveSource(taskId, configModel);
    	
    	// 保存目标数据信息
    	this.saveTarget(taskId, configModel);
    	
    	// 保存输出通道
    	DataTaskCoreEntity channelOut = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.CHANNEL,
    			DataServiceUtil.OUTPUT, "name", 1);
    	this.saveCore(taskId, DataServiceUtil.CHANNEL + "." + DataServiceUtil.OUTPUT, channelOut, 1);
    	
    	// 保存输入通道
    	DataTaskCoreEntity channelIn = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.CHANNEL,
    			DataServiceUtil.INPUT, "name", 1);
    	this.saveCore(taskId, DataServiceUtil.CHANNEL + "." + DataServiceUtil.INPUT, channelIn, 1);
    	
    	// 保存交换机
    	DataTaskCoreEntity exchange = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.EXCHANGE,
    			DataServiceUtil.EXCHANGE, "name", 1);
    	this.saveCore(taskId, DataServiceUtil.EXCHANGE, exchange, 1);
    	
    	// 保存执行器
    	DataTaskCoreEntity executorInput = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.EXECUTOR,
    			DataServiceUtil.INPUT, "name", 1);
    	this.saveCore(taskId, DataServiceUtil.EXECUTOR + "." + DataServiceUtil.INPUT, executorInput, 1);
    	
    	DataTaskCoreEntity executorOut = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.EXECUTOR,
    			DataServiceUtil.OUTPUT, "name", 1);
    	this.saveCore(taskId, DataServiceUtil.EXECUTOR + "." + DataServiceUtil.OUTPUT, executorOut, 1);
    	
    	// 保存其它配置
    	int index = 1;
    	List<DataTaskCoreEntity> printLogs = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyName(taskId, DataServiceUtil.PLUGS,
    			DataServiceUtil.PRINTLOG, "name");
    	if(printLogs != null && printLogs.size() > 0) {
    		for(DataTaskCoreEntity dataTaskCoreEntity : printLogs) {
    			this.saveCore(taskId, DataServiceUtil.PLUGS, dataTaskCoreEntity, index);
    			index++;
    		}
    	}
    	
    	List<DataTaskCoreEntity> dirtyDatas = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyName(taskId, DataServiceUtil.PLUGS,
    			DataServiceUtil.DIRTYDATA, "name");
    	if(dirtyDatas != null && dirtyDatas.size() > 0) {
    		for(DataTaskCoreEntity dataTaskCoreEntity : dirtyDatas) {
    			this.saveCore(taskId, DataServiceUtil.PLUGS, dataTaskCoreEntity, index);
    			index++;
    		}
    	}
    	
    	// 保存异字段配置
    	DataTaskCoreEntity different = dataTaskCoreRepository.findByTaskIdAndTypeNameAndDataTypeAndKeyNameAndSequence(taskId, DataServiceUtil.PLUGS,
    			DataServiceUtil.DIFFERENT, "field", 1);
    	if(different != null) {
    		DataTaskMakeUpEntity dataTaskMakeUpEntity = new DataTaskMakeUpEntity();
        	dataTaskMakeUpEntity.setId(Y9IdGenerator.genId());
        	dataTaskMakeUpEntity.setTaskId(taskId);
        	dataTaskMakeUpEntity.setTypeName(DataServiceUtil.PLUGS);
        	dataTaskMakeUpEntity.setNameValue(DataServiceUtil.DIFFERENTCLASS);
        	List<DifferentField> differentFields = Y9JsonUtil.readList(different.getValue(), DifferentField.class);
        	Map<String, Object> map = new HashMap<String, Object>();
        	Map<String, Object> rmap = new HashMap<String, Object>();
        	for(DifferentField field : differentFields) {
        		DataTableField dtf1 = dataTableFieldRepository.findById(field.getSource()).orElse(null);
        		DataTableField dtf2 = dataTableFieldRepository.findById(field.getTarget()).orElse(null);
        		rmap.put(dtf1.getName(), dtf2.getName());
        	}
        	map.put("renameMap", rmap);
        	dataTaskMakeUpEntity.setArgsValue(Y9JsonUtil.writeValueAsString(map));
        	dataTaskMakeUpEntity.setTabIndex(index);
        	dataTaskMakeUpRepository.save(dataTaskMakeUpEntity);
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
    	DataSourceEntity dataSourceEntity = dataSourceRepository.findById(configModel.getTargetId()).orElse(null);
    	map.put("jdbcUrl", dataSourceEntity.getUrl());
    	map.put("userName", dataSourceEntity.getUsername());
    	map.put("password", dataSourceEntity.getPassword());
    	// 获取目标表信息
    	DataTable dataTable = dataTableRepository.findById(configModel.getTargetTable()).orElse(null);
    	map.put("tableName", dataTable.getName());
    	
    	if(configModel.getWriterType().equals("update")) {
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
    	dataTaskMakeUpEntity.setArgsValue(Y9JsonUtil.writeValueAsString(map));
    	dataTaskMakeUpEntity.setTabIndex(1);
    	dataTaskMakeUpRepository.save(dataTaskMakeUpEntity);
    }

}
