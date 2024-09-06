package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.listener.TaskMakeUpListener;
import net.risesoft.pojo.ConvertField;
import net.risesoft.pojo.DateField;
import net.risesoft.pojo.DifferentField;
import net.risesoft.pojo.TaskConfigModel;
import net.risesoft.pojo.TaskCoreModel;
import net.risesoft.pojo.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataTaskService;
import net.risesoft.util.DataServiceUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.DataBusinessEntity;
import net.risesoft.y9public.entity.DataInterfaceEntity;
import net.risesoft.y9public.entity.DataInterfaceParamsEntity;
import net.risesoft.y9public.entity.DataSourceEntity;
import net.risesoft.y9public.entity.DataTable;
import net.risesoft.y9public.entity.DataTableField;
import net.risesoft.y9public.entity.DataTaskConfigEntity;
import net.risesoft.y9public.entity.DataTaskCoreEntity;
import net.risesoft.y9public.entity.DataTaskEntity;
import net.risesoft.y9public.repository.DataBusinessRepository;
import net.risesoft.y9public.repository.DataInterfaceParamsRepository;
import net.risesoft.y9public.repository.DataInterfaceRepository;
import net.risesoft.y9public.repository.DataMappingRepository;
import net.risesoft.y9public.repository.DataSourceRepository;
import net.risesoft.y9public.repository.DataTableFieldRepository;
import net.risesoft.y9public.repository.DataTableRepository;
import net.risesoft.y9public.repository.DataTaskConfigRepository;
import net.risesoft.y9public.repository.DataTaskCoreRepository;
import net.risesoft.y9public.repository.DataTaskRepository;
import net.risesoft.y9public.repository.spec.DataTaskSpecification;

@Service(value = "dataTaskService")
@Slf4j
@RequiredArgsConstructor
public class DataTaskServiceImpl implements DataTaskService {
	
	private final DataTaskRepository dataTaskRepository;
	private final DataTaskConfigRepository dataTaskConfigRepository;
	private final DataTaskCoreRepository dataTaskCoreRepository;
	private final DataBusinessRepository dataBusinessRepository;
	private final DataSourceRepository dataSourceRepository;
	private final DataTableRepository dataTableRepository;
	private final DataTableFieldRepository dataTableFieldRepository;
	private final DataMappingRepository dataMappingRepository;
	private final TaskMakeUpListener taskMakeUpListener;
	private final DataInterfaceParamsRepository dataInterfaceParamsRepository;
	private final DataInterfaceRepository dataInterfaceRepository;
	
	@Override
	public Page<DataTaskEntity> findPage(List<String> ids, String name, List<String> businessIds, int page, int rows) {
		if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "createTime"));
        DataTaskSpecification spec = new DataTaskSpecification(ids, businessIds, name);
		return dataTaskRepository.findAll(spec, pageable);
	}

	@Override
	public TaskModel getById(String id) {
		TaskModel taskModel = new TaskModel();
		DataTaskEntity dataTask = dataTaskRepository.findById(id).orElse(null);
		if(dataTask != null) {
			taskModel.setId(dataTask.getId());
			taskModel.setName(dataTask.getName());
			taskModel.setDescription(dataTask.getDescription());
			taskModel.setBusinessId(dataTask.getBusinessId());
			taskModel.setUserId(dataTask.getUserId());
			taskModel.setUserName(dataTask.getUserName());
			
			TaskConfigModel taskConfigModel = new TaskConfigModel();
			DataTaskConfigEntity taskConfig = dataTaskConfigRepository.findByTaskId(dataTask.getId());
			Y9BeanUtil.copyProperties(taskConfig, taskConfigModel);
			taskModel.setTaskConfigModel(taskConfigModel);
			
			List<TaskCoreModel> taskCoreModels = new ArrayList<TaskCoreModel>();
			List<DataTaskCoreEntity> taskCoreList = dataTaskCoreRepository.findByTaskId(dataTask.getId());
			for(DataTaskCoreEntity taskCore : taskCoreList) {
				String taskId = taskCore.getId();
				if(taskId.indexOf("-") == -1) {
					TaskCoreModel taskCoreModel = new TaskCoreModel();
					Y9BeanUtil.copyProperties(taskCore, taskCoreModel);
					taskCoreModels.add(taskCoreModel);
				}
				// 数据脱敏字段
				if(taskId.startsWith(DataServiceUtil.MASK)) {
					taskModel.setMaskFields(taskCore.getValue());
				}
				// 数据加密字段
				if(taskId.startsWith(DataServiceUtil.ENCRYP)) {
					taskModel.setEncrypFields(taskCore.getValue());
				}
				// 异字段
				if(taskId.startsWith(DataServiceUtil.DIFFERENT)) {
					taskModel.setDifferentField(Y9JsonUtil.readList(taskCore.getValue(), DifferentField.class));
				}
				// 日期格式
				if(taskId.startsWith(DataServiceUtil.DATE)) {
					taskModel.setDateField(Y9JsonUtil.readList(taskCore.getValue(), DateField.class));
				}
				// 数据转换
				if(taskId.startsWith(DataServiceUtil.CONVERT)) {
					taskModel.setConvertField(Y9JsonUtil.readList(taskCore.getValue(), ConvertField.class));
				}
			}
			taskModel.setTaskCoreList(taskCoreModels);
		}
		return taskModel;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteData(String id) {
		dataTaskRepository.deleteById(id);
		DataTaskConfigEntity taskConfig = dataTaskConfigRepository.findByTaskId(id);
		if(taskConfig != null) {
			dataTaskConfigRepository.delete(taskConfig);
		}
		List<DataTaskCoreEntity> taskCoreList = dataTaskCoreRepository.findByTaskId(id);
		if(taskCoreList != null && taskCoreList.size() > 0) {
			dataTaskCoreRepository.deleteAll(taskCoreList);
		}
	}

	@Override
	public List<DataTaskEntity> findByBusinessId(String businessId) {
		return dataTaskRepository.findByBusinessId(businessId);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataTaskEntity> saveData(DataTaskEntity entity) {
		if (entity != null && StringUtils.isNotBlank(entity.getName())) {
			if (StringUtils.isBlank(entity.getId())) {
				entity.setId(Y9IdGenerator.genId());
			}
			entity.setUserId(Y9LoginUserHolder.getPersonId());
			entity.setUserName(Y9LoginUserHolder.getUserInfo().getName());
			return Y9Result.success(dataTaskRepository.save(entity), "保存成功");
		}
		return Y9Result.failure("数据不能为空");
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataTaskEntity> save(TaskModel taskModel) {
		// 保存主体
		DataTaskEntity entity = null;
		if (StringUtils.isBlank(taskModel.getId())) {
			entity = new DataTaskEntity();
			entity.setId(Y9IdGenerator.genId());
		}else {
			entity = dataTaskRepository.findById(taskModel.getId()).orElse(null);
		}
		entity.setBusinessId(taskModel.getBusinessId());
		entity.setDescription(taskModel.getDescription());
		entity.setName(taskModel.getName());
		entity.setUserId(Y9LoginUserHolder.getPersonId());
		entity.setUserName(Y9LoginUserHolder.getUserInfo().getName());
		dataTaskRepository.save(entity);
		
		// 保存config
		TaskConfigModel configModel = taskModel.getTaskConfigModel();
		if(configModel != null) {
			if(StringUtils.isBlank(configModel.getId())) {
				configModel.setId(Y9IdGenerator.genId());
				configModel.setTaskId(entity.getId());
			}
			DataTaskConfigEntity configEntity = new DataTaskConfigEntity();
			Y9BeanUtil.copyProperties(configModel, configEntity);
			dataTaskConfigRepository.save(configEntity);
		}
		
		// 保存core，先删后加
		List<DataTaskCoreEntity> taskCoreList = dataTaskCoreRepository.findByTaskId(entity.getId());
		if(taskCoreList != null && taskCoreList.size() > 0) {
			dataTaskCoreRepository.deleteAll(taskCoreList);
		}
		List<TaskCoreModel> coreList = taskModel.getTaskCoreList();
		if(coreList != null && coreList.size() > 0) {
			DataTaskCoreEntity coreEntity = null;
			for(TaskCoreModel coreModel : coreList) {
				if(StringUtils.isBlank(coreModel.getId())) {
					coreModel.setId(Y9IdGenerator.genId());
					coreModel.setTaskId(entity.getId());
				}
				coreEntity = new DataTaskCoreEntity();
				Y9BeanUtil.copyProperties(coreModel, coreEntity);
				dataTaskCoreRepository.save(coreEntity);
			}
		}
		
		// 数据脱敏字段
		String maskFields = taskModel.getMaskFields();
		if(StringUtils.isNotBlank(maskFields)) {
			DataTaskCoreEntity taskCore = new DataTaskCoreEntity();
			taskCore.setId(DataServiceUtil.MASK + "-" + entity.getId());
			taskCore.setTaskId(entity.getId());
			taskCore.setTypeName("plugs");
			taskCore.setKeyName("field");
			taskCore.setDataType(DataServiceUtil.MASK);
			taskCore.setValue(maskFields);
			taskCore.setSequence(1);
			dataTaskCoreRepository.save(taskCore);
		}
		
		// 数据加密字段
		String encrypFields = taskModel.getEncrypFields();
		if(StringUtils.isNotBlank(encrypFields)) {
			DataTaskCoreEntity taskCore = new DataTaskCoreEntity();
			taskCore.setId(DataServiceUtil.ENCRYP + "-" + entity.getId());
			taskCore.setTaskId(entity.getId());
			taskCore.setTypeName("plugs");
			taskCore.setKeyName("field");
			taskCore.setDataType(DataServiceUtil.ENCRYP);
			taskCore.setValue(encrypFields);
			taskCore.setSequence(1);
			dataTaskCoreRepository.save(taskCore);
		}
		
		// 日期格式
		List<DateField> dateField = taskModel.getDateField();
		if(dateField != null && dateField.size() > 0) {
			DataTaskCoreEntity taskCore = new DataTaskCoreEntity();
			taskCore.setId(DataServiceUtil.DATE + "-" + entity.getId());
			taskCore.setTaskId(entity.getId());
			taskCore.setTypeName("plugs");
			taskCore.setKeyName("format");
			taskCore.setDataType(DataServiceUtil.DATE);
			taskCore.setValue(Y9JsonUtil.writeValueAsString(dateField));
			taskCore.setSequence(1);
			dataTaskCoreRepository.save(taskCore);
		}
		
		// 异字段
		List<DifferentField> differentField = taskModel.getDifferentField();
		if(differentField != null && differentField.size() > 0) {
			DataTaskCoreEntity taskCore = new DataTaskCoreEntity();
			taskCore.setId(DataServiceUtil.DIFFERENT + "-" + entity.getId());
			taskCore.setTaskId(entity.getId());
			taskCore.setTypeName("plugs");
			taskCore.setKeyName("field");
			taskCore.setDataType(DataServiceUtil.DIFFERENT);
			taskCore.setValue(Y9JsonUtil.writeValueAsString(differentField));
			taskCore.setSequence(1);
			dataTaskCoreRepository.save(taskCore);
		}
		
		// 数据转换
		List<ConvertField> convertField = taskModel.getConvertField();
		if(convertField != null && convertField.size() > 0) {
			DataTaskCoreEntity taskCore = new DataTaskCoreEntity();
			taskCore.setId(DataServiceUtil.CONVERT + "-" + entity.getId());
			taskCore.setTaskId(entity.getId());
			taskCore.setTypeName("plugs");
			taskCore.setKeyName("field");
			taskCore.setDataType(DataServiceUtil.CONVERT);
			taskCore.setValue(Y9JsonUtil.writeValueAsString(convertField));
			taskCore.setSequence(1);
			dataTaskCoreRepository.save(taskCore);
		}
		
		taskMakeUpListener.onTaskMakeUp(entity.getId(), entity.getName(), configModel);
		LOGGER.debug("任务["+entity.getId()+"]-配置保存成功");
		
		return Y9Result.success(entity, "保存成功");
	}

	@Override
	public void deleteTaskCore(String id) {
		if(dataTaskCoreRepository.existsById(id)) {
			dataTaskCoreRepository.deleteById(id);
		}
	}

	@Override
	public Map<String, Object> getTaskDetails(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			DataTaskEntity dataTask = dataTaskRepository.findById(id).orElse(null);
			if(dataTask != null) {
				map.put("id", dataTask.getId());
				map.put("name", dataTask.getName());
				map.put("description", dataTask.getDescription());
				map.put("userName", dataTask.getUserName());
				map.put("createTime", dataTask.getCreateTime());
				
				DataBusinessEntity business = dataBusinessRepository.findById(dataTask.getBusinessId()).orElse(null);
				map.put("business", business.getName());
				
				DataTaskConfigEntity taskConfig = dataTaskConfigRepository.findByTaskId(dataTask.getId());
				if(taskConfig.getSourceType().equals("api")) {
					map.put("sourceName", "接口");
					DataInterfaceEntity dataInterfaceEntity = dataInterfaceRepository.findById(taskConfig.getSourceTable()).orElse(null);
					map.put("sourceTable", dataInterfaceEntity.getInterfaceName() + "(" + dataInterfaceEntity.getInterfaceUrl() + ")");
				}else {
					DataSourceEntity source = dataSourceRepository.findById(taskConfig.getSourceId()).orElse(null);
					map.put("sourceName", "[" + source.getBaseType() + "]" + source.getBaseName());
					DataTable sourceTable = dataTableRepository.findById(taskConfig.getSourceTable()).orElse(null);
					map.put("sourceTable", sourceTable.getCname() + "(" + sourceTable.getName() + ")");
				}
				map.put("sourceClass", dataMappingRepository.findById(taskConfig.getSourceName()).orElse(null).getClassName());
				List<String> sourceCloumn = new ArrayList<String>();
				String[] sourceCloumns = taskConfig.getSourceCloumn().split(",");
				for(String field : sourceCloumns) {
					if(taskConfig.getSourceType().equals("api")) {
						DataInterfaceParamsEntity dataParamsEntity = dataInterfaceParamsRepository.findById(field).orElse(null);
						if(dataParamsEntity != null) {
							sourceCloumn.add(dataParamsEntity.getRemark() + "(" + dataParamsEntity.getParamName() + ")");
						}
					}else {
						DataTableField tableField = dataTableFieldRepository.findById(field).orElse(null);
						if(tableField != null) {
							sourceCloumn.add(tableField.getCname() + "(" + tableField.getName() + ")");
						}
					}
				}
				map.put("sourceCloumn", sourceCloumn);
				map.put("fetchSize", taskConfig.getFetchSize());
				map.put("whereSql", taskConfig.getWhereSql());
				if(StringUtils.isNotBlank(taskConfig.getSplitPk())) {
					DataTableField tableField = dataTableFieldRepository.findById(taskConfig.getSplitPk()).orElse(null);
					if(tableField != null) {
						map.put("splitPk", tableField.getName());
					}else {
						map.put("splitPk", "字段获取不到");
					}
				}else {
					map.put("splitPk", "");
				}
				map.put("precise", taskConfig.getPrecise());
				map.put("tableNumber", taskConfig.getTableNumber());
				map.put("splitFactor", taskConfig.getSplitFactor());
				
				if(taskConfig.getTargetType().equals("api")) {
					map.put("tagertName", "接口");
					DataInterfaceEntity dataInterfaceEntity = dataInterfaceRepository.findById(taskConfig.getTargetTable()).orElse(null);
					map.put("tagertTable", dataInterfaceEntity.getInterfaceName() + "(" + dataInterfaceEntity.getInterfaceUrl() + ")");
				}else {
					DataSourceEntity tagert = dataSourceRepository.findById(taskConfig.getTargetId()).orElse(null);
					map.put("tagertName", "[" + tagert.getBaseType() + "]" + tagert.getBaseName());
					DataTable tagertTable = dataTableRepository.findById(taskConfig.getTargetTable()).orElse(null);
					map.put("tagertTable", tagertTable.getCname() + "(" + tagertTable.getName() + ")");
				}
				map.put("tagertClass", dataMappingRepository.findById(taskConfig.getTargeName()).orElse(null).getClassName());
				List<String> targetCloumn = new ArrayList<String>();
				String[] targetCloumns = taskConfig.getTargetCloumn().split(",");
				for(String field : targetCloumns) {
					if(taskConfig.getTargetType().equals("api")) {
						DataInterfaceParamsEntity dataParamsEntity = dataInterfaceParamsRepository.findById(field).orElse(null);
						if(dataParamsEntity != null) {
							targetCloumn.add(dataParamsEntity.getRemark() + "(" + dataParamsEntity.getParamName() + ")");
						}
					}else {
						DataTableField tableField = dataTableFieldRepository.findById(field).orElse(null);
						if(tableField != null) {
							targetCloumn.add(tableField.getCname() + "(" + tableField.getName() + ")");
						}
					}
				}
				map.put("targetCloumn", targetCloumn);
				map.put("writerType", taskConfig.getWriterType());
				String text = "";
				if(StringUtils.isNotBlank(taskConfig.getUpdateField())) {
					String[] ids = taskConfig.getUpdateField().split(",");
					for(String field : ids) {
						DataTableField tableField = dataTableFieldRepository.findById(field).orElse(null);
						if(tableField != null) {
							text += StringUtils.isBlank(text)?tableField.getName():","+tableField.getName();
						}else {
							text += StringUtils.isBlank(text)?"字段获取不到":",字段获取不到";
						}
					}
				}
				map.put("updateField", text);
				
				List<TaskCoreModel> taskCoreModels = new ArrayList<TaskCoreModel>();
				List<DataTaskCoreEntity> taskCoreList = dataTaskCoreRepository.findByTaskId(dataTask.getId());
				for(DataTaskCoreEntity taskCore : taskCoreList) {
					String taskId = taskCore.getId();
					if(taskId.indexOf("-") == -1) {
						taskCore.setDataType(DataServiceUtil.getTitle(taskCore.getDataType()));
						TaskCoreModel taskCoreModel = new TaskCoreModel();
						Y9BeanUtil.copyProperties(taskCore, taskCoreModel);
						taskCoreModels.add(taskCoreModel);
					}
					// 数据脱敏字段
					else if(taskId.startsWith(DataServiceUtil.MASK)) {
						map.put("maskFields", taskCore.getValue());
					}
					// 数据加密字段
					else if(taskId.startsWith(DataServiceUtil.ENCRYP)) {
						map.put("encrypFields", taskCore.getValue());
					}
					// 异字段
					else if(taskId.startsWith(DataServiceUtil.DIFFERENT)) {
						List<DifferentField> diffList = Y9JsonUtil.readList(taskCore.getValue(), DifferentField.class);
						for(DifferentField diff : diffList) {
							if(taskConfig.getSourceType().equals("api")) {
								diff.setSource(dataInterfaceParamsRepository.findById(diff.getSource()).orElse(null).getParamName());
							}else {
								diff.setSource(dataTableFieldRepository.findById(diff.getSource()).orElse(null).getName());
							}
							if(taskConfig.getTargetType().equals("api")) {
								diff.setTarget(dataInterfaceParamsRepository.findById(diff.getTarget()).orElse(null).getParamName());
							}else {
								diff.setTarget(dataTableFieldRepository.findById(diff.getTarget()).orElse(null).getName());
							}
						}
						map.put("differentField", diffList);
					}
					// 日期格式
					else if(taskId.startsWith(DataServiceUtil.DATE)) {
						List<DateField> dateList = Y9JsonUtil.readList(taskCore.getValue(), DateField.class);
						for(DateField date : dateList) {
							if(taskConfig.getSourceType().equals("api")) {
								date.setFieldName(dataInterfaceParamsRepository.findById(date.getFieldName()).orElse(null).getParamName());
							}else {
								date.setFieldName(dataTableFieldRepository.findById(date.getFieldName()).orElse(null).getName());
							}
						}
						map.put("format", dateList);
					}
					// 数据转换
					else if(taskId.startsWith(DataServiceUtil.CONVERT)) {
						List<ConvertField> convertList = Y9JsonUtil.readList(taskCore.getValue(), ConvertField.class);
						for(ConvertField convert : convertList) {
							if(taskConfig.getSourceType().equals("api")) {
								convert.setFieldName(dataInterfaceParamsRepository.findById(convert.getFieldName()).orElse(null).getParamName());
							}else {
								convert.setFieldName(dataTableFieldRepository.findById(convert.getFieldName()).orElse(null).getName());
							}
						}
						map.put("convertField", convertList);
					}
				}
				map.put("taskCoreList", taskCoreModels);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public List<DataTaskEntity> findAll(String businessId) {
		if(StringUtils.isNotBlank(businessId)) {
			return dataTaskRepository.findByBusinessId(businessId);
		}
		return dataTaskRepository.findAll();
	}

	@Override
	public DataTaskEntity findById(String id) {
		return dataTaskRepository.findById(id).orElse(null);
	}

}