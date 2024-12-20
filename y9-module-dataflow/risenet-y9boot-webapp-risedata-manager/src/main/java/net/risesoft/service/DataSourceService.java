package net.risesoft.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.api.utils.jdbc.filedTypeMapping.TypeDefinition;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.DataSourceTypeEntity;
import net.risesoft.y9public.entity.DataSourceEntity;
import net.risesoft.y9public.entity.DataTable;
import net.risesoft.y9public.entity.DataTableField;

public interface DataSourceService {

	/**
	 * 查询数据源分页列表
	 */
	Page<DataSourceEntity> getDataSourcePage(String baseName, int page, int rows);

	/**
	 * 保存数据源实体类
	 * 
	 */
	DataSourceEntity saveDataSource(DataSourceEntity entity);

	/**
	 * 根据ID获取数据源
	 * 
	 * @param id ：数据源主键id
	 * @return
	 */
	DataSourceEntity getDataSourceById(String id);

	/**
	 * 根据id删除数据源
	 * 
	 * @param id ：数据源主键id
	 */
	Y9Result<String> deleteDataSource(String id);

	/**
	 * 获取根据ID获取要测试的数据源
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	DataSource getDataSource(String id);

	/**
	 * 根据数据源名称查询
	 * @param baseName
	 * @return
	 */
	DataSourceEntity findByBaseName(String baseName);
	
	/**
	 * 根据类别获取数据源列表
	 * @param baseType
	 * @return
	 */
	List<DataSourceEntity> findByBaseType(String baseType);
	
	/**
	 * 获取数据源列表：1-获取所有，0-获取非表的数据源
	 * @return
	 */
	List<DataSourceEntity> findByType(Integer type);
	
	/**
	 * 搜索数据源
	 * @param baseName
	 * @return
	 */
	List<Map<String, Object>> searchSource(String baseName);
	
	/**
	 * 获取表信息
	 * @param baseId
	 * @param page
	 * @param limit
	 * @return
	 */
	Page<DataTable> findAllTable(String baseId, String name, Integer page, Integer limit);
	
	/**
	 * 根据数据源获取需要提取的表
	 * @param baseId
	 * @param tableName
	 * @return
	 */
	Map<String, Object> getNotExtractList(String baseId, String tableName);
	
	/**
	 * 提取数据库表信息
	 * @param baseId
	 * @param tableName
	 * @param dataSourceEntity
	 * @return
	 */
	Y9Result<String> extractTable(String baseId, String tableName, DataSourceEntity dataSourceEntity);
	
	/**
	 * 提取elastic表信息
	 * @param baseId
	 * @param tableName
	 * @param dataSourceEntity
	 * @return
	 */
	Y9Result<String> extractIndex(String baseId, String tableName, DataSourceEntity dataSourceEntity);
	
	/**
	 * 获取数据源分类列表
	 * @return
	 */
	List<DataSourceTypeEntity> findDataCategory();
	
	/**
	 * 保存分类信息
	 * @param entity
	 * @return
	 */
	Y9Result<DataSourceTypeEntity> saveDataCategory(MultipartFile iconFile, DataSourceTypeEntity entity);
	
	/**
	 * 删除表信息
	 * @param id
	 */
	Y9Result<String> deleteTable(String id);
	
	/**
	 * 根据表id获取字段列表
	 * @param tableId
	 * @param page
	 * @param limit
	 * @return
	 */
	Page<DataTableField> findAll(String tableId, Integer page, Integer limit);
	
	/**
	 * 根据id获取表信息
	 * @param id
	 * @return
	 */
	DataTable getTableById(String id);
	
	/**
	 * 保存表信息
	 * @param entity
	 * @return
	 */
	Y9Result<DataTable> saveTable(DataTable entity);
	
	/**
	 * 保存字段信息
	 * @param entity
	 * @return
	 */
	Y9Result<DataTableField> saveField(DataTableField entity);
	
	/**
	 * 删除字段信息
	 * @param id
	 */
	void deleteField(String id);
	
	/**
	 * 删除数据源分类
	 * @param id
	 */
	Y9Result<String> deleteCategory(String id);
	
	/**
	 * 生成表
	 * @param tableId
	 * @return
	 */
	Y9Result<String> buildTable(String tableId);
	
	/**
	 * 批量保存字段
	 * @param table
	 * @param fieldList
	 * @return
	 */
	Y9Result<String> saveFields(List<DataTableField> fieldList);
	
	/**
	 * 根据表id获取字段列表
	 * @param tableId
	 * @return
	 */
	List<DataTableField> getTableColumns(String tableId, String name);
	
	/**
	 * 根据数据源获取表列表
	 * @param sourceId
	 * @return
	 */
	List<DataTable> getTableList(String sourceId);

	/**
	 * 根据数据源获取字段类型、字段长度、字段对应 jdbc.types值 列表
	 * @param sourceId
	 * @return
	 */
	Y9Result<List<TypeDefinition>> getFieldTypes(String sourceId);
	
	/**
	 * 获取数据表的任务关系数据
	 * @param tableId
	 * @return
	 */
	Y9Result<List<Map<String, Object>>> getTableJob(String tableId);

}
