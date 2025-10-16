package net.risesoft.service.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jodd.util.Base64;
import lombok.RequiredArgsConstructor;
import net.risesoft.api.utils.jdbc.filedTypeMapping.JdbcTypeMappingCache;
import net.risesoft.api.utils.jdbc.filedTypeMapping.TypeDefinition;
import net.risesoft.elastic.client.ElasticsearchRestClient;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataSourceService;
import net.risesoft.util.DataConstant;
import net.risesoft.util.sqlddl.DDL;
import net.risesoft.util.sqlddl.DbColumn;
import net.risesoft.util.sqlddl.DbMetaDataUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataSourceTypeEntity;
import net.risesoft.y9public.entity.DataSourceEntity;
import net.risesoft.y9public.entity.DataTable;
import net.risesoft.y9public.entity.DataTableField;
import net.risesoft.y9public.entity.DataTaskConfigEntity;
import net.risesoft.y9public.entity.DataTaskEntity;
import net.risesoft.y9public.repository.DataSourceTypeRepository;
import net.risesoft.y9public.repository.DataSourceRepository;
import net.risesoft.y9public.repository.DataTableFieldRepository;
import net.risesoft.y9public.repository.DataTableRepository;
import net.risesoft.y9public.repository.DataTaskConfigRepository;
import net.risesoft.y9public.repository.DataTaskRepository;
import net.risesoft.y9public.repository.spec.DataTableSpecification;

@Service(value = "dataSourceService")
@RequiredArgsConstructor
public class DataSourceServiceImpl implements DataSourceService {

	private final DataSourceRepository datasourceRepository;

	private final DataTableRepository dataTableRepository;

	private final DataTableFieldRepository dataTableFieldRepository;

	private final DataSourceTypeRepository dataSourceTypeRepository;
	
	private final DataTaskConfigRepository dataTaskConfigRepository;
	
	private final DataTaskRepository dataTaskRepository;

	@Override
	public Page<DataSourceEntity> getDataSourcePage(String baseName, int page, int rows) {
		if (page < 0) {
			page = 1;
		}
		Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "createTime"));
		if (StringUtils.isNotBlank(baseName)) {
			return datasourceRepository.findByBaseNameContainingAndTenantId(baseName, Y9LoginUserHolder.getTenantId(), pageable);
		}
		return datasourceRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = false)
	public DataSourceEntity saveDataSource(DataSourceEntity entity) {
		DataSourceEntity df = null;
		if (entity != null) {
			if (StringUtils.isBlank(entity.getId())) {
				entity.setId(Y9IdGenerator.genId());
			}else {
				df = datasourceRepository.findById(entity.getId()).orElse(null);
				if(df != null && entity.getPassword().equals("******")) {
					entity.setPassword(df.getPassword());
				}
			}
			if (entity.getIsLook() == null) {
				entity.setIsLook(0);
			}
			DataSourceTypeEntity category = dataSourceTypeRepository.findByName(entity.getBaseType());
			if(StringUtils.isBlank(entity.getDriver())) {
				entity.setDriver(category.getDriver());
			}
			entity.setType(category.getType());
			if(entity.getType() == 0) {
				if(entity.getInitialSize() == null) {
					entity.setInitialSize(1);
				}
				if(entity.getMaxActive() == null) {
					entity.setMaxActive(20);
				}
				if(entity.getMinIdle() == null) {
					entity.setMinIdle(1);
				}
			}
			entity.setTenantId(Y9LoginUserHolder.getTenantId());
			entity.setUserId(Y9LoginUserHolder.getPersonId());
			df = datasourceRepository.save(entity);
		}
		return df;
	}

	@Override
	public DataSourceEntity getDataSourceById(String id) {
		return datasourceRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> deleteDataSource(String id) {
		int num = dataTableRepository.findByBaseId(id).size();
		if (num > 0) {
			return Y9Result.failure("该数据源下存在表信息，无法删除");
		}
		datasourceRepository.deleteById(id);
		return Y9Result.successMsg("删除成功");
	}

	@Override
	public DataSourceEntity findByBaseName(String lookupName) {
		return datasourceRepository.findByBaseNameAndTenantId(lookupName, Y9LoginUserHolder.getTenantId());
	}

	@Override
	public List<DataSourceEntity> findByType(Integer type) {
		if (type.equals(0)) {
			List<DataSourceEntity> list = datasourceRepository.findByTypeAndTenantId(type, Y9LoginUserHolder.getTenantId());
			list.addAll(datasourceRepository.findByBaseTypeAndTenantIdOrderByCreateTime(DataConstant.ES, Y9LoginUserHolder.getTenantId()));
			return list;
		}
		return datasourceRepository.findByTenantId(Y9LoginUserHolder.getTenantId());
	}

	@Override
	public Page<DataTable> findAllTable(String baseId, String name, Integer page, Integer limit) {
		if (page < 0) {
			page = 1;
		}
		Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createTime"));
		List<String> baseIds = new ArrayList<String>();
		if(StringUtils.isBlank(baseId)) {
			baseIds = datasourceRepository.findIdByTenantId(Y9LoginUserHolder.getTenantId());
		}else {
			baseIds.add(baseId);
		}
		if(baseIds.size() == 0) {
			return null;
		}
		DataTableSpecification spec = new DataTableSpecification(baseIds, name);
		return dataTableRepository.findAll(spec, pageable);
	}

	@Override
	public Map<String, Object> getNotExtractList(String baseId, String tableName) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		if (StringUtils.isBlank(baseId)) {
			map.put("count", listMap.size());
			map.put("msg", "");
			map.put("code", 0);
			map.put("data", listMap);
			return map;
		}
		DataSourceEntity source = datasourceRepository.findById(baseId).orElse(null);
		List<String> tableList = new ArrayList<String>();
		if(StringUtils.isNotBlank(tableName)) {
			tableList = dataTableRepository.findByBaseIdAndNameLike(baseId, "%" + tableName + "%");
		}else {
			tableList = dataTableRepository.findByBaseId(baseId);
		}
		List<String> list = new ArrayList<String>();
		if(source.getBaseType().equals(DataConstant.ES)) {
			ElasticsearchRestClient elasticsearchRestClient = new ElasticsearchRestClient(source.getUrl(), source.getUsername(), source.getPassword());
			try {
				list = elasticsearchRestClient.getIndexs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			list = DbMetaDataUtil.getAllTable(source.getDriver(), source.getUsername(), source.getPassword(), source.getUrl(), source.getBaseSchema());
		}
		for (String str : list) {
			if (StringUtils.isNotBlank(tableName) && !str.contains(tableName)) {
				continue;
			}
			Map<String, Object> row = new HashMap<String, Object>();
			if (tableList.contains(str)) {
				row.put("status", 1);// 已提取
			} else {
				row.put("status", 0);// 未提取
			}
			row.put("name", str);
			row.put("baseId", baseId);
			listMap.add(row);
		}
		map.put("count", listMap.size());
		map.put("msg", "");
		map.put("code", 0);
		map.put("data", listMap);
		return map;
	}

	@Override
	public Y9Result<String> extractTable(String baseId, String tableName, DataSourceEntity dataSourceEntity) {
		Connection connection = null;
		ResultSet tables = null;
		ResultSet columns = null;
		ResultSet pk = null;
		try {
			// 加载驱动
			Class.forName(dataSourceEntity.getDriver());
			Properties props = new Properties();
			props.setProperty("user", dataSourceEntity.getUsername());
			props.setProperty("password", dataSourceEntity.getPassword());
			props.setProperty("remarks", "true"); // 设置可以获取remarks信息
			props.setProperty("useInformationSchema", "true");// 设置可以获取tables remarks信息
			// 获得数据库连接
			connection = DriverManager.getConnection(dataSourceEntity.getUrl(), props);
			// 获得元数据
			DatabaseMetaData metaData = connection.getMetaData();
			// 获取模式
			String schema = StringUtils.isBlank(dataSourceEntity.getBaseSchema()) ? null : dataSourceEntity.getBaseSchema();
			// 判断表名称是否携带所属schema
			String name = tableName;// 先存储原数据
			if(tableName.indexOf(".") != -1) {
				String[] tableNames = tableName.split("\\.");
				schema = tableNames[0].toUpperCase();
				tableName = tableNames[1].toUpperCase();
			}
			// 获取数据库类型
			String dialect = metaData.getDatabaseProductName().toLowerCase();
			if ("mysql".equals(dialect) || "microsoft".equals(dialect)) {
				tables = metaData.getTables(connection.getCatalog(), null, tableName, new String[] { "TABLE", "VIEW" });
			} else {
				tables = metaData.getTables(null, schema, tableName, new String[] { "TABLE", "VIEW" });
			}
			while (tables.next()) {
				if (tables.getString("TABLE_NAME").equals(tableName)) {
					DataTable table = dataTableRepository.findByBaseIdAndName(baseId, name);
					if(table == null) {
						table = new DataTable();
						table.setId(Y9IdGenerator.genId());
					}
					table.setBaseId(baseId);
					table.setName(name);
					if (StringUtils.isNotBlank(tables.getString("REMARKS"))) {
						table.setCname(tables.getString("REMARKS"));
					} else {
						table.setCname(tableName);
					}
					table.setStatus(1);

					// 通过表名获得所有字段名
					columns = metaData.getColumns(null, schema, tableName, "%");
					// 获取表所有主键
					pk = metaData.getPrimaryKeys(schema, null, tableName);
					List<String> pkList = new ArrayList<String>();
					while (pk.next()) {
						String na = pk.getString("COLUMN_NAME");
						pkList.add(na);
					}
					int count = 0;
					while (columns.next()) {
						String column_name = columns.getString("COLUMN_NAME");// 获得字段名
						String columnName = columns.getString("REMARKS");// 获得字段中文名
						String type_name = columns.getString("TYPE_NAME");// 获得字段类型名称
						int dataType = columns.getInt("DATA_TYPE"); // 对应的java.sql.Types类型
						String decimalDigits = columns.getString("DECIMAL_DIGITS");// 小数位数
						
						DataTableField field = dataTableFieldRepository.findByTableIdAndName(table.getId(), column_name);
						if(field == null) {
							field = new DataTableField();
							field.setId(Y9IdGenerator.genId());
						}
						field.setName(column_name);
						if (StringUtils.isNotBlank(columnName)) {
							field.setCname(columnName);
						} else {
							field.setCname(column_name);
						}
						field.setFieldNull(columns.getString("IS_NULLABLE"));
						if (pkList.contains(column_name)) {
							field.setFieldPk("Y");
						} else {
							field.setFieldPk("N");
						}
						field.setFieldType(type_name);
						field.setTypeNum(dataType);
						field.setTableId(table.getId());
						if(type_name.equals("LONGTEXT") || type_name.equals("DATETIME") || type_name.equals("DATE") 
								|| type_name.equals("TIMESTAMP")) {
							field.setFieldLength("");
						}else {
							int columnSize = columns.getInt("COLUMN_SIZE");// 列大小
							if (StringUtils.isNotBlank(decimalDigits)) {
								field.setFieldLength(columnSize + "," + decimalDigits);
							} else {
								field.setFieldLength(columnSize + "");
							}
						}
						field.setIsState(true);
						dataTableFieldRepository.save(field);
						count = count + 1;
					}
					table.setTableCount(count);
					dataTableRepository.save(table);
					
					return Y9Result.successMsg("提取成功");
				}
			}
			return Y9Result.failure("查无此表");
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("提取失败：" + e.getMessage());
		} finally {
			if (tables != null) {
				try {
					tables.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (columns != null) {
				try {
					columns.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pk != null) {
				try {
					pk.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataSourceTypeEntity> saveDataCategory(MultipartFile iconFile, DataSourceTypeEntity entity) {
		if (entity != null && StringUtils.isNotBlank(entity.getName())) {
			DataSourceTypeEntity category = dataSourceTypeRepository.findByName(entity.getName());
			if (category != null && !entity.getId().equals(category.getId())) {
				return Y9Result.failure("该分类已存在！");
			}
			if (StringUtils.isBlank(entity.getId())) {
				entity.setId(Y9IdGenerator.genId());
			} else {
				DataSourceTypeEntity dataSourceTypeEntity = dataSourceTypeRepository.findById(entity.getId()).orElse(null);
				if (dataSourceTypeEntity != null && !dataSourceTypeEntity.getName().equals(entity.getName())) {
					long count = datasourceRepository.countByBaseType(dataSourceTypeEntity.getName());
					if (count > 0) {
						return Y9Result.failure("该分类存在关联数据，名称无法修改！");
					}
				}
			}
			if (iconFile != null && iconFile.getSize() != 0) {
				byte[] b = null;
				try {
					b = iconFile.getBytes();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				entity.setImgData(Base64.encodeToString(b));
			}
			return Y9Result.success(dataSourceTypeRepository.save(entity), "保存成功");
		}
		return Y9Result.failure("数据不能为空");
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> deleteTable(String id) {
		if(dataTaskConfigRepository.findByTableId(id).size() > 0) {
			return Y9Result.failure("已关联任务，无法删除");
		}
		dataTableFieldRepository.deleteByTableId(id);
		dataTableRepository.deleteById(id);
		return Y9Result.successMsg("删除成功");
	}

	@Override
	public Page<DataTableField> findAll(String tableId, Integer page, Integer limit) {
		if (page < 0) {
			page = 1;
		}
		Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createTime"));
		if (StringUtils.isBlank(tableId)) {
			return null;
		}
		return dataTableFieldRepository.findByTableId(tableId, pageable);
	}

	@Override
	public DataTable getTableById(String id) {
		return dataTableRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataTableField> saveField(DataTableField entity) {
		DataTableField data = null;
		if (entity != null) {
			if (StringUtils.isBlank(entity.getId())) {
				DataTableField field = dataTableFieldRepository.findByTableIdAndName(entity.getTableId(), entity.getName());
				if (field != null) {
					Y9Result.failure("字段名称已存在");
				}
				entity.setId(Y9IdGenerator.genId());
				entity.setIsState(false);
				entity.setUpdateVersion(0);
				Integer maxOrder = dataTableFieldRepository.getMaxTabIndex(entity.getTableId());
				entity.setDisplayOrder(maxOrder == null ? 1 : maxOrder + 1);
			}else {
				DataTableField oldData = dataTableFieldRepository.findById(entity.getId()).orElse(null);
				if (oldData != null) {
					entity.setUpdateVersion(oldData.getUpdateVersion() == null ? 0 : oldData.getUpdateVersion() + 1);
					if(entity.getDisplayOrder() == null) {
						entity.setDisplayOrder(oldData.getDisplayOrder());
					}
					entity.setIsState(oldData.getIsState());
				}
			}
			data = dataTableFieldRepository.save(entity);
		}
		return Y9Result.success(data, "保存成功");
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteField(String id) {
		dataTableFieldRepository.deleteById(id);
	}

	@Override
	public List<DataSourceTypeEntity> findDataCategory() {
		return dataSourceTypeRepository.findAll();
	}

	@Override
	public List<DataSourceEntity> findByBaseType(String baseType) {
		return datasourceRepository.findByBaseTypeAndTenantIdOrderByCreateTime(baseType, Y9LoginUserHolder.getTenantId());
	}

	@Override
	public List<Map<String, Object>> searchSource(String baseName) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		List<DataSourceTypeEntity> list = dataSourceTypeRepository.findAll();
		for (DataSourceTypeEntity category : list) {
			List<DataSourceEntity> sourceList = datasourceRepository.findByBaseNameContainingAndBaseTypeAndTenantId(baseName,
					category.getName(), Y9LoginUserHolder.getTenantId());
			if (sourceList.size() == 0) {
				continue;
			}
			List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
			for (DataSourceEntity source : sourceList) {
				Map<String, Object> rmap = new HashMap<String, Object>();
				rmap.put("id", source.getId());
				rmap.put("baseName", source.getBaseName());
				children.add(rmap);
			}
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("category", category);
			row.put("children", children);
			listMap.add(row);
		}
		return listMap;
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> deleteCategory(String id) {
		DataSourceTypeEntity dataSourceTypeEntity = dataSourceTypeRepository.findById(id).orElse(null);
		if (dataSourceTypeEntity != null) {
			long count = datasourceRepository.countByBaseType(dataSourceTypeEntity.getName());
			if (count > 0) {
				return Y9Result.failure("该分类存在关联数据，无法删除！");
			}
			dataSourceTypeRepository.deleteById(id);
			return Y9Result.successMsg("删除成功");
		}
		return Y9Result.failure("数据不存在，请刷新数据");
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataTable> saveTable(DataTable entity) {
		try {
			if (entity != null && StringUtils.isNotBlank(entity.getName())) {
				DataSourceEntity source = getDataSourceById(entity.getBaseId());
				if (source == null) {
					return Y9Result.failure("数据源不存在");
				}
				if (source.getType() != 0) {
					if (StringUtils.isNotBlank(entity.getId())) {
						DataTable table = getTableById(entity.getId());
						if(table != null) {
							table.setCname(entity.getCname());
							table.setName(entity.getName());
							return Y9Result.success(dataTableRepository.save(table), "保存成功，该类型数据源暂只支持修改名称，不涉及物理表操作");
						}
					}
					return Y9Result.failure("暂不支持的数据源类型");
				}
				if (StringUtils.isBlank(entity.getId())) {
					// 判断是否已存在表
					DataTable dataTable = dataTableRepository.findByBaseIdAndName(entity.getBaseId(), entity.getName());
					if (dataTable != null) {
						return Y9Result.failure("已存在该表名称");
					}
					entity.setId(Y9IdGenerator.genId());
					// 检查表在库中是否存在，已存在不需重新生成
					boolean exist = DbMetaDataUtil.checkTableExist(DbMetaDataUtil.get(source.getDriver(), source.getUsername(), 
							source.getPassword(), source.getUrl()), entity.getName(), true);
					if (exist) {
						entity.setStatus(1);
					}else {
						entity.setStatus(0);
					}
				}else {
					DataTable table = getTableById(entity.getId());
					if(table != null) {
						// 判断是否修改了表名称
						if(!table.getName().equals(entity.getName())) {
							// 判断是否已存在表
							DataTable dataTable = dataTableRepository.findByBaseIdAndName(entity.getBaseId(), entity.getName());
							if (dataTable != null) {
								return Y9Result.failure("已存在该表名称");
							}
							// 判断修改的表名称在库中是否存在，已存在不需重新生成
							boolean exist = DbMetaDataUtil.checkTableExist(DbMetaDataUtil.get(source.getDriver(), source.getUsername(), 
									source.getPassword(), source.getUrl()), entity.getName(), true);
							if (exist) {
								entity.setStatus(1);
							}else {
								entity.setStatus(0);
								// 判断原数据在库里是否生成，没生成无需记录修改的原名称
								if(table.getStatus() == 1) {
									// 将原名称存储
									entity.setOldName(table.getName());
								}else {
									entity.setOldName(table.getOldName());
								}
							}
						}else {
							entity.setStatus(table.getStatus());
						}
						if(entity.getTableCount() == null) {
							entity.setTableCount(table.getTableCount());
						}
						if(entity.getServiceTable() == null) {
							entity.setServiceTable(table.getServiceTable());
						}
					}else {
						return Y9Result.failure("修改表信息失败，信息不存在");
					}
				}
				return Y9Result.success(dataTableRepository.save(entity), "保存成功");
			} else {
				return Y9Result.failure("数据不能为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Y9Result.failure("程序出错");
	}

	@Override
	public Y9Result<String> buildTable(String tableId) {
		try {
			DataTable table = getTableById(tableId);
			if (table != null) {
				DataSourceEntity source = getDataSourceById(table.getBaseId());
				if (source.getType() != 0) {
					return Y9Result.failure("暂不支持的数据源类型");
				}
				if(table.getStatus() == 1) {
					return Y9Result.failure("当前表状态显示已创建，如果想修改表结构，请点击修改表结构按钮");
				}
				String msg = "表创建成功";
				if(StringUtils.isNotBlank(table.getOldName())) {
					// 修改表名称
					DDL.renameTable(DbMetaDataUtil.get(source.getDriver(), source.getUsername(), 
							source.getPassword(), source.getUrl()), table.getOldName(), table.getName());
					table.setOldName("");
					msg = "表重命名成功";
				}else {
					// 查询表字段信息
					List<DataTableField> fieldList = dataTableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
					// 生成建表字段列表
					List<DbColumn> columnList = getDbColumn(fieldList);
					// 建表
					DDL.addTableColumn(DbMetaDataUtil.get(source.getDriver(), source.getUsername(), 
							source.getPassword(), source.getUrl()), table.getName(), table.getCname(), Y9JsonUtil.writeValueAsString(columnList));
					// 更新字段状态信息
					for (DataTableField field : fieldList) {
						field.setIsState(true);
						dataTableFieldRepository.save(field);
					}
				}
				// 更新表状态信息
				table.setStatus(1);
				dataTableRepository.save(table);
				return Y9Result.successMsg(msg);
			}
			return Y9Result.failure("表信息不存在");
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("表生成失败:" + e.getMessage());
		}
	}
	
	/**
	 * 获取要创建的字段
	 * @param fieldList
	 * @return
	 */
	private List<DbColumn> getDbColumn(List<DataTableField> fieldList) {
		List<DbColumn> dbcs = new ArrayList<DbColumn>();
		for (DataTableField fieldTemp : fieldList) {
			DbColumn dbColumn = new DbColumn();
			dbColumn.setColumn_name(fieldTemp.getName());
			dbColumn.setColumn_name_old(fieldTemp.getOldName());
			dbColumn.setNullable("YES".equals(fieldTemp.getFieldNull()) ? true : false);
			dbColumn.setIsCreateIndex(false);
			dbColumn.setType_name(fieldTemp.getFieldType());
			dbColumn.setData_type(fieldTemp.getTypeNum());
			//不能有长度的字段置为0
			dbColumn.setData_length(fieldTemp.getFieldLength());
			dbColumn.setComment(fieldTemp.getCname());
			dbColumn.setPrimaryKey("Y".equals(fieldTemp.getFieldPk()) ? true : false);
			dbColumn.setIsState(fieldTemp.getIsState()==null ? false:fieldTemp.getIsState());
			dbcs.add(dbColumn);
		}
		return dbcs;
	}
	
	@Override
	public Y9Result<String> updateTable(String tableId) {
		try {
			DataTable table = getTableById(tableId);
			if (table != null) {
				DataSourceEntity source = getDataSourceById(table.getBaseId());
				if (source.getType() != 0) {
					return Y9Result.failure("暂不支持的数据源类型");
				}
				if(table.getStatus() == 0) {
					return Y9Result.failure("当前表状态显示未创建，检查是否没生成表或者修改了表名称没点生成表按钮更新");
				}
				// 查询表字段信息
				List<DataTableField> fieldList = dataTableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
				// 生成建表字段列表
				List<DbColumn> columnList = getDbColumn(fieldList);
				// 更新表结构
				DDL.addTableColumn(DbMetaDataUtil.get(source.getDriver(), source.getUsername(), 
						source.getPassword(), source.getUrl()), table.getName(), table.getCname(), Y9JsonUtil.writeValueAsString(columnList));
				// 更新字段状态信息
				for (DataTableField field : fieldList) {
					field.setIsState(true);
					field.setOldName("");
					dataTableFieldRepository.save(field);
				}
				return Y9Result.successMsg("更新成功");
			}
			return Y9Result.failure("表信息不存在");
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("修改失败:" + e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> saveFields(List<DataTableField> fieldList) {
		String tableId = "";
		for (DataTableField field : fieldList) {
			if(StringUtils.isBlank(tableId)) {
				tableId = field.getTableId();
			}
			saveField(field);
		}
		// 保存字段数量
		if(StringUtils.isNotBlank(tableId)) {
			DataTable table = getTableById(tableId);
			if(table != null) {
				table.setTableCount(fieldList.size());
				dataTableRepository.save(table);
			}
		}
		return Y9Result.successMsg("保存成功");
	}

	@Override
	public List<DataTableField> getTableColumns(String tableId, String name) {
		if (StringUtils.isNotBlank(name)) {
			return dataTableFieldRepository.findByTableIdAndNameContaining(tableId, name);
		}
		return dataTableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
	}

	@Override
	public List<DataTable> getTableList(String sourceId) {
		return dataTableRepository.findByBaseIdOrderByName(sourceId);
	}

	@Override
	public Y9Result<List<TypeDefinition>> getFieldTypes(String sourceId) {
		DataSourceEntity dataEntity = datasourceRepository.findById(sourceId).orElse(null);
		if (dataEntity != null) {
			if(dataEntity.getType() == 1) {
				return Y9Result.success(null, "数据源类型不支持");
			}
			List<TypeDefinition> fieldTypes = JdbcTypeMappingCache.getFieldType(dataEntity.getDriver(),
					dataEntity.getUrl(), dataEntity.getUsername(), dataEntity.getPassword(), dataEntity.getBaseType());
			return Y9Result.success(fieldTypes, "获取字段类型成功");
		}
		return Y9Result.failure("获取字段类型失败");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Y9Result<String> extractIndex(String baseId, String tableName, DataSourceEntity dataSourceEntity) {
		try {
			ElasticsearchRestClient elasticsearchRestClient = new ElasticsearchRestClient(dataSourceEntity.getUrl(), 
					dataSourceEntity.getUsername(), dataSourceEntity.getPassword());
			String data = elasticsearchRestClient.getMapping(tableName);
			if(!data.equals("failed")) {
				Map<String, Object> map = Y9JsonUtil.readHashMap(data);
				Map<String, Object> mappings = (Map<String, Object>) map.get(tableName);
				Map<String, Object> properties = (Map<String, Object>) mappings.get("mappings");
				Map<String, Object> dataMap = (Map<String, Object>) properties.get("properties");
				// 低版本的es存在type值，需要先获取type
				if(dataMap == null) {
					Map<String, Object> type = (Map<String, Object>) properties.values().stream().findFirst().get();
					dataMap = (Map<String, Object>) type.get("properties");
				}
				// 保存表信息
				DataTable table = dataTableRepository.findByBaseIdAndName(baseId, tableName);
				if(table == null) {
					table = new DataTable();
					table.setId(Y9IdGenerator.genId());
				}
				table.setBaseId(baseId);
				table.setName(tableName);
				table.setCname(tableName);
				table.setStatus(1);
				// 保存字段信息
				int count = 0;
				for (Map.Entry<String, Object> entry : dataMap.entrySet()) {  
					String column_name = entry.getKey();// 获得字段名
//					if("_class".equals(column_name)) {
//						continue;
//					}
					Map<String, Object> value = (Map<String, Object>) entry.getValue();
					String type_name = (String)value.get("type");// 获得字段类型名称
					
					DataTableField field = dataTableFieldRepository.findByTableIdAndName(table.getId(), column_name);
				    if(field == null) {
				    	field = new DataTableField();
				    	field.setId(Y9IdGenerator.genId());
				    }
					field.setName(column_name);
					field.setCname(column_name);
					field.setFieldType(type_name);
					field.setTableId(table.getId());
					field.setIsState(true);
					dataTableFieldRepository.save(field);
					count = count + 1;
				}
				table.setTableCount(count);
				dataTableRepository.save(table);
				return Y9Result.successMsg("提取成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Y9Result.failure("提取失败");
	}

	@Override
	public Y9Result<List<Map<String, Object>>> getTableJob(String tableId) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
		try {
			DataTable dataTable = dataTableRepository.findById(tableId).orElse(null);
			if(dataTable != null) {
				// 获取任务信息
				List<DataTaskConfigEntity> dataTaskList = dataTaskConfigRepository.findByTableId(tableId);
				for(DataTaskConfigEntity taskConfigEntity : dataTaskList) {
					Map<String, Object> map = new HashMap<String, Object>();
					DataTaskEntity dataTaskEntity = dataTaskRepository.findById(taskConfigEntity.getTaskId()).orElse(null);
					map.put("taskName", dataTaskEntity.getName());
					// 源表信息
					String sourceTable = dataTable.getName();
					if(taskConfigEntity.getSourceTable().equals(tableId)) {
						map.put("sourceTable", dataTable.getName());
					}else {
						DataTable dataTable2 = dataTableRepository.findById(taskConfigEntity.getSourceTable()).orElse(null);
						map.put("sourceTable", dataTable2.getName() + "(" + getDataSourceById(taskConfigEntity.getSourceId()).getBaseName() + ")");
						sourceTable = dataTable2.getName();
					}
					// 获取表数据量
					if(taskConfigEntity.getSourceType().equals(DataConstant.ES)) {
						map.put("sourceTableNum", getElasticCount(taskConfigEntity.getSourceId(), sourceTable));
					}else {
						map.put("sourceTableNum", getTableDataCount(taskConfigEntity.getSourceId(), sourceTable));
					}
					// 目的表信息
					String targetTable = dataTable.getName();
					if(taskConfigEntity.getTargetTable().equals(tableId)) {
						map.put("targetTable", dataTable.getName());
					}else {
						DataTable dataTable2 = dataTableRepository.findById(taskConfigEntity.getTargetTable()).orElse(null);
						map.put("targetTable", dataTable2.getName() + "(" + getDataSourceById(taskConfigEntity.getTargetId()).getBaseName() + ")");
						
						targetTable = dataTable2.getName();
					}
					// 获取表数据量
					if(taskConfigEntity.getTargetType().equals(DataConstant.ES)) {
						map.put("targetTableNum", getElasticCount(taskConfigEntity.getTargetId(), targetTable));
					}else {
						map.put("targetTableNum", getTableDataCount(taskConfigEntity.getTargetId(), targetTable));
					}
					listMap.add(map);
				}
			}
		} catch (Exception e) {
			return Y9Result.failure("获取失败：" + e.getMessage());
		}
		return Y9Result.success(listMap, "获取成功");
	}
	
	private long getTableDataCount(String sourceId, String tableName) {
		DataSourceEntity source = getDataSourceById(sourceId);
		return DbMetaDataUtil.getTableDataNum(DbMetaDataUtil.get(source.getDriver(), source.getUsername(), 
				source.getPassword(), source.getUrl()), tableName);
	}
	
	private long getElasticCount(String sourceId, String tableName) {
		DataSourceEntity source = getDataSourceById(sourceId);
		ElasticsearchRestClient elasticsearchRestClient = new ElasticsearchRestClient(source.getUrl(), 
				source.getUsername(), source.getPassword());
		try {
			return elasticsearchRestClient.getCount(tableName, "{}");
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	public Y9Result<String> copyTable(String baseId, String tableName, String id) {
		try {
			DataTable table = dataTableRepository.findByBaseIdAndName(baseId, tableName);
			if (table != null) {
				// 查询表字段信息
				List<DataTableField> fieldList = dataTableFieldRepository.findByTableIdOrderByDisplayOrderAsc(table.getId());
				// 生成建表字段列表
				List<DbColumn> columnList = getDbColumn(fieldList);
				// 建表
				DataSourceEntity source = getDataSourceById(id);
				DDL.addTableColumn(DbMetaDataUtil.get(source.getDriver(), source.getUsername(), 
						source.getPassword(), source.getUrl()), table.getName(), table.getCname(), Y9JsonUtil.writeValueAsString(columnList));
				// 提取表
				extractTable(id, tableName, source);
				return Y9Result.successMsg("复制表成功");
			}
			return Y9Result.failure("表信息不存在，只能复制已提取的表");
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("复制表失败：" + e.getMessage());
		}
	}

	@Override
	public Y9Result<String> copyIndex(String baseId, String tableName, String id) {
		try {
			// 获取目标库
			DataSourceEntity dataSourceEntity = getDataSourceById(baseId);
			if(!dataSourceEntity.getBaseType().equals(DataConstant.ES)) {
				return Y9Result.failure("elastic只支持复制elastic表");
			}
			DataTable table = dataTableRepository.findByBaseIdAndName(baseId, tableName);
			if (table == null) {
				return Y9Result.failure("复制前，请先提取表信息");
			}
			ElasticsearchRestClient elasticsearchRestClient = new ElasticsearchRestClient(dataSourceEntity.getUrl(),
					dataSourceEntity.getUsername(), dataSourceEntity.getPassword());
			// 获取索引表字段
			String mapping = elasticsearchRestClient.getMapping(tableName);
			if(mapping.equals("failed")) {
				return Y9Result.failure("复制失败，请查看日志处理");
			}
			Map<String, Object> map = Y9JsonUtil.readHashMap(mapping);
			String mapping2 = Y9JsonUtil.writeValueAsString(map.get(tableName));
			// 获取创建库
			DataSourceEntity sourceEntity = getDataSourceById(id);
			ElasticsearchRestClient elasticsearchRestClient2 = new ElasticsearchRestClient(sourceEntity.getUrl(),
					sourceEntity.getUsername(), sourceEntity.getPassword());
			// 创建索引表
			String data = elasticsearchRestClient2.createIndexAndMapping(tableName, mapping2);
			if(data.equals("failed")) {
				return Y9Result.failure("复制失败，请查看日志处理");
			}
			// 提取表
			extractIndex(id, tableName, sourceEntity);
			return Y9Result.successMsg("复制表成功");
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("复制表失败：" + e.getMessage());
		}
	}

}