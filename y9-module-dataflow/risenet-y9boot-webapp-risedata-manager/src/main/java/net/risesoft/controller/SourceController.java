package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.utils.jdbc.filedTypeMapping.TypeDefinition;
import net.risesoft.elastic.client.ElasticsearchRestClient;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataInterfaceService;
import net.risesoft.service.DataMappingService;
import net.risesoft.service.DataSourceService;
import net.risesoft.util.DataConstant;
import net.risesoft.util.sqlddl.DbMetaDataUtil;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataSourceTypeEntity;
import net.risesoft.y9public.entity.DataInterfaceEntity;
import net.risesoft.y9public.entity.DataSourceEntity;
import net.risesoft.y9public.entity.DataTable;
import net.risesoft.y9public.entity.DataTableField;

@Validated
@RestController
@RequestMapping(value = "/api/rest/source", produces = "application/json")
@RequiredArgsConstructor
public class SourceController {

	private final DataSourceService dataSourceService;
	private final DataMappingService dataMappingService;
	private final DataInterfaceService dataInterfaceService;
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取数据源分类列表", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/findCategoryAll")
    public Y9Result<List<DataSourceTypeEntity>> findCategoryAll() {
        List<DataSourceTypeEntity> list = dataSourceService.findDataCategory();
        return Y9Result.success(list, "获取成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据类别获取数据源列表", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/findByBaseType")
    public Y9Result<List<DataSourceEntity>> findByBaseType(@RequestParam String category) {
        List<DataSourceEntity> list = dataSourceService.findByBaseType(category);
        list.stream().map((item) -> {
        	if(StringUtils.isNotBlank(item.getPassword())) {
        		item.setPassword("******");
        	}
            return item;
        }).collect(Collectors.toList());
        return Y9Result.success(list, "获取成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取数据源列表", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/findByType")
    public Y9Result<List<DataSourceEntity>> findByType(Integer type) {
        return Y9Result.success(dataSourceService.findByType(type), "获取成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "搜索数据源列表", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/searchSource")
    public Y9Result<List<Map<String, Object>>> searchSource(String baseName) {
        return Y9Result.success(dataSourceService.searchSource(baseName), "获取成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存数据源分类信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveDataCategory")
	public Y9Result<DataSourceTypeEntity> saveDataCategory(MultipartFile iconFile, DataSourceTypeEntity entity) {
		return dataSourceService.saveDataCategory(iconFile, entity);
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除数据源分类信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteCategory")
	public Y9Result<String> deleteCategory(@RequestParam String id) {
		return dataSourceService.deleteCategory(id);
	}
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存数据源连接信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveSource")
	public Y9Result<String> saveSource(DataSourceEntity entity) {
		dataSourceService.saveDataSource(entity);
		return Y9Result.successMsg("保存成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据id获取数据源信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getDataSource")
	public Y9Result<DataSourceEntity> getDataSource(String id) {
		DataSourceEntity dataSourceEntity = dataSourceService.getDataSourceById(id);
		if(dataSourceEntity != null && StringUtils.isNotBlank(dataSourceEntity.getPassword())) {
			dataSourceEntity.setPassword("******");
		}
		return Y9Result.success(dataSourceEntity, "获取成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除数据源", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteSource")
	public Y9Result<String> deleteSource(@RequestParam String id) {
		return dataSourceService.deleteDataSource(id);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "分页获取数据库的表信息数据", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/getTableAll")
	public Y9Page<Map<String, Object>> getTableAll(String baseId, String name, Integer page, Integer size) {
        Page<DataTable> pageList = dataSourceService.findAllTable(baseId, name, page, size);
        if(pageList == null) {
        	return Y9Page.success(page, 0, 0, null, "获取数据成功");
        }
        // 并行解析数据
        List<CompletableFuture<Map<String, Object>>> futures = pageList.stream()  
                .map(n -> CompletableFuture.supplyAsync(() -> getDataMap(n))) // 创建异步任务  
                .collect(Collectors.toList()); // 收集所有的 CompletableFuture
        // 等待所有任务完成，获取结果
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        List<Map<String, Object>> listMap = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), listMap, "获取数据成功");
    }
	
	private Map<String, Object> getDataMap(DataTable record) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("id", record.getId());
			map.put("name", record.getName());
			map.put("cname", record.getCname());
			map.put("baseId", record.getBaseId());
			DataSourceEntity source = dataSourceService.getDataSourceById(record.getBaseId());
			map.put("baseType", source.getBaseType());
			map.put("baseName", source.getBaseName());
			if(record.getStatus() == 1) {
				if(source.getType() == 0) {
					map.put("dataNum", DbMetaDataUtil.getTableDataNum(DbMetaDataUtil.get(source.getDriver(), source.getUsername(), 
							source.getPassword(), source.getUrl()), record.getName()));
				}else if(source.getBaseType().equals(DataConstant.ES)) {
					ElasticsearchRestClient elasticsearchRestClient = new ElasticsearchRestClient(source.getUrl(), 
							source.getUsername(), source.getPassword());
					try {
						map.put("dataNum", elasticsearchRestClient.getCount(record.getName(), "{}"));
					} catch (Exception e) {
						map.put("dataNum", e.getMessage());
					}
				}
			}else {
				map.put("dataNum", 0);
			}
			map.put("status", record.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return map;
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取数据库需要提取的表", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/getNotExtractList")
    public Map<String, Object> getNotExtractList(String baseId, String tableName) {
		Map<String, Object> map = dataSourceService.getNotExtractList(baseId, tableName);
		return map;
	}
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "提取表信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping("/extractTable")
    public Y9Result<String> extractTable(@RequestParam String baseId, @RequestParam String tableName) {
		DataSourceEntity dataSourceEntity = dataSourceService.getDataSourceById(baseId);
		if(dataSourceEntity.getType() == 0) {
			return dataSourceService.extractTable(baseId, tableName, dataSourceEntity);
		}else if(dataSourceEntity.getBaseType().equals(DataConstant.ES)) {
			return dataSourceService.extractIndex(baseId, tableName, dataSourceEntity);
		}
		return Y9Result.failure("不支持的类型");
	}
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "复制表", logLevel = LogLevelEnum.RSLOG)
	@PostMapping("/copyTable")
    public Y9Result<String> copyTable(@RequestParam String baseId, @RequestParam String tableName, @RequestParam String id) {
		DataSourceEntity dataSourceEntity = dataSourceService.getDataSourceById(id);
		if(dataSourceEntity.getType() == 0) {
			return dataSourceService.copyTable(baseId, tableName, id);
		}else if(dataSourceEntity.getBaseType().equals(DataConstant.ES)) {
			return dataSourceService.copyIndex(baseId, tableName, id);
		}
		return Y9Result.failure("不支持的类型");
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除表信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteTable")
	public Y9Result<String> deleteTable(@RequestParam String id) {
		return dataSourceService.deleteTable(id);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "分页获取表字段信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/getFieldAll")
	public Y9Page<DataTableField> getFieldAll(String tableId, Integer page, Integer size) {
		Page<DataTableField> pageList = dataSourceService.findAll(tableId, page, size);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(), "获取数据成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取表字段信息列表", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/getFieldList")
	public Y9Result<List<DataTableField>> getFieldList(@RequestParam String tableId, String name) {
        return Y9Result.success(dataSourceService.getTableColumns(tableId, name), "获取数据成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存表信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveTable")
	public Y9Result<DataTable> saveTable(DataTable entity) {
		return dataSourceService.saveTable(entity);
	}
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存表字段信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveField")
	public Y9Result<DataTableField> saveField(DataTableField entity) {
		return dataSourceService.saveField(entity);
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除表字段信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteField")
	public Y9Result<String> deleteField(@RequestParam String id) {
		dataSourceService.deleteField(id);
		return Y9Result.successMsg("删除成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "生成表", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/buildTable")
	public Y9Result<String> buildTable(@RequestParam String tableId) {
		return dataSourceService.buildTable(tableId);
	}
	
	@RiseLog(operationType = OperationTypeEnum.MODIFY, operationName = "修改表结构", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/updateTable")
	public Y9Result<String> updateTable(@RequestParam String tableId) {
		return dataSourceService.updateTable(tableId);
	}
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "批量保存字段", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveFields")
	public Y9Result<String> saveFields(@RequestBody String fields) {
		return dataSourceService.saveFields(Y9JsonUtil.readList(fields, DataTableField.class));
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据数据源id获取表列表和执行类", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getTableList")
	public Y9Result<Map<String, Object>> getTableList(String sourceId, String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 接口源
		if(sourceId.equals("api")) {
			List<Map<String, Object>> listMap = new ArrayList<>();
			List<DataInterfaceEntity> interfaceList = null;
			if(StringUtils.isNotBlank(type)) {
				// 任务配置里的输入对应接口里的输出，输出对应输入
				interfaceList = dataInterfaceService.findByPattern(type.equals("input") ? 0 : 1);
				
				// 获取执行类
				map.put("classList", dataMappingService.findByTypeNameAndFuncType("api", type));
			}else {
				interfaceList = dataInterfaceService.findAll();
			}
			for(DataInterfaceEntity dataInterfaceEntity : interfaceList) {
				Map<String, Object> rmap = new HashMap<String, Object>();
				rmap.put("id", dataInterfaceEntity.getId());
				rmap.put("name", dataInterfaceEntity.getInterfaceName());
				listMap.add(rmap);
			}
			map.put("tableList", listMap);
		}else {
			DataSourceEntity source = dataSourceService.getDataSourceById(sourceId);
			if(source == null) {
				return Y9Result.failure("数据源不存在");
			}
			if(source.getBaseType().equals("ftp")) {
				map.put("tableList", source.getDirectory());
			}else {
				map.put("tableList", dataSourceService.getTableList(sourceId));
			}
			map.put("classList", dataMappingService.findByTypeNameAndFuncType(source.getBaseType(), type));
		}
		return Y9Result.success(map, "获取数据成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "检测数据源状态", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/checkStatus")
	public Y9Result<Boolean> checkStatus(String sourceId) {
		DataSourceEntity source = dataSourceService.getDataSourceById(sourceId);
		if(source != null && source.getType() == 0) {
			return Y9Result.success(DbMetaDataUtil.getConnection(source.getDriver(), source.getUsername(), source.getPassword(), source.getUrl()));
		}
		return Y9Result.success(true);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取表关联的任务", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getTableJob")
	public Y9Result<List<Map<String, Object>>> getTableJob(String tableId) {
		return dataSourceService.getTableJob(tableId);
	}

	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取数据源可选字段类型、长度、以及对应的types", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getFieldTypes")
	public Y9Result<List<TypeDefinition>> getFieldTypes(String sourceId) {
		return dataSourceService.getFieldTypes(sourceId);
	}
}
