package net.risesoft.api.persistence.config.impl;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.commons.map.LMap;
import net.risedata.jdbc.factory.OperationBuilderFactory;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.operation.impl.CustomOperation;
import net.risedata.jdbc.search.LPageable;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.api.exceptions.ServiceOperationException;
import net.risesoft.api.persistence.config.ConfigHisService;
import net.risesoft.api.persistence.config.ConfigService;
import net.risesoft.api.persistence.dao.ConfigDao;
import net.risesoft.api.persistence.log.LogService;
import net.risesoft.api.persistence.model.config.Config;
import net.risesoft.api.persistence.model.config.ConfigHis;
import net.risesoft.api.persistence.model.log.Log;
import net.risesoft.api.security.SecurityManager;
import net.risesoft.api.utils.AutoIdUtil;
import net.risesoft.api.utils.PropertiesUtil;
import net.risesoft.api.utils.SqlUtils;
import net.risesoft.y9public.entity.DataTaskMakeUpEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * @Description : 配置服务
 * @ClassName ConfigServiceImpl
 * @Author lb
 * @Date 2022/8/5 10:24
 * @Version 1.0
 */
@Service
public class ConfigServiceImpl extends AutomaticCrudService<Config, String> implements ConfigService {

	@Autowired
	ConfigHisService configHisService;

	@Autowired
	ConfigDao configDao;

	@Override
	@Transactional
	public void saveConfig(Config config) {
		String id = configDao.find(config.getName(), config.getGroup(), config.getEnvironment());
		if (id != null && !id.equals(config.getId())) {
			throw new ServiceOperationException("唯一id 重复已存在 该配置文件");
		}
		if (StringUtils.isEmpty(config.getId())) {
			config.setId(AutoIdUtil.getRandomId26());
			config.setCreateDate(new Date());
			config.setUpdateDate(new Date());
			configHisService.saveConfig(config, "新增");
			insert(config);
		} else {
			config.setUpdateDate(new Date());
			configHisService.saveConfig(config, "修改");
			updateById(config);
		}
	}

	@Override
	public List<String> findIdAll(String[] groups, String[] configs, String environment) {
		return configDao.findIdAll(groups, configs, environment);
	}

	@Override
	public LPage<Map<String, Object>> search(Config config, LPageable pageable) {
		return getSearchExecutor().searchForPage(config,
				"ID id,NAME name,TYPE config_type,GROUP_NAME config_group,CREATE_DATE,UPDATE_DATE,ENVIRONMENT environment,DESCRIPTION DESCRIPTION",
				pageable);
	}

	@Override
	public void delConfigById(String id) {

		Config config = new Config();
		config.setId(id);

		delete(config);
	}

	@Override
	public boolean back(String hisId, String id) {
		ConfigHis his = configHisService.findOne(hisId);
		if (his == null) {
			throw new ServiceOperationException(" 历史版本 不存在!");
		}
		Config current = getOne(id);
		if (current != null) {
			current.setContent(his.getContent());
			updateById(current);
		} else {
			throw new ServiceOperationException(" 当前配置文件 不存在!");
		}
		return true;
	}

	@Override
	public List<Config> findByIds(String[] ids) {
		Map<String, Operation> operationMap = OperationBuilderFactory.builder("id", new CustomOperation((where) -> {
			where.append("id in (");
			for (int i = 0; i < ids.length; i++) {
				if (i != 0) {
					where.append(",?");
				} else {
					where.append("?");
				}
				where.add(ids[i]);
			}
			where.append(")");
			return true;
		}));
		return search("*", null, operationMap);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

	@Autowired
	LogService logService;

	@Override
	public Map<String, Object> getConfigMap(String[] groups, String[] configs, String environment, String serviceId,
			String ipAddress, boolean append) {
		List<Config> configList = configDao.findAll(groups, configs, environment);
		Log log = new Log();
		String configStr = Arrays.toString(configs);
		log.setIp(ipAddress);
		log.setContext(serviceId + "获取了:" + configStr + "分组为:" + Arrays.toString(groups));
		log.setType(Config.LOG_TYPE);
		log.setInsId(configStr);
		log.setEnvironment(environment);
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> tempMap;
		for (String group : groups) {
			for (String config : configs) {
				for (Config config1 : configList) {
					if (config1.getName().equals(config) && config1.getGroup().equals(group)) {
						try {
							tempMap = PropertiesUtil.getMap(config1);
							tempMap.forEach((k, v) -> {
								if (append) {
									Object value = result.get(k);
									if (StringUtils.isEmpty(value)) {
										result.put(k, v);
									} else {
										value += "," + v;
										result.put(k, value);
									}
								} else {
									result.put(k, v);
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							LOGGER.error(config1.getName() + "error: " + e.getMessage());
							throw new ServiceOperationException(config1.getName() + "" + e.getMessage());
						}
					}
				}
			}
		}
		logService.addLog(log, ipAddress);
		return result;
	}

	@Override
	@Transactional
	public boolean syncEnvironment(String sourceConfigIds, String toEnvironment) {
		String[] sourceConfigIdes = sourceConfigIds.split(",");
		for (String sourceConfigId : sourceConfigIdes) {
			Config config = getOne(sourceConfigId);
			if (config == null) {
				throw new ServiceOperationException("不存在的配置文件");
			}
			String ovlId = configDao.find(config.getName(), config.getGroup(), toEnvironment);
			config.setEnvironment(toEnvironment);
			config.setId(null);
			if (ovlId != null) {
				config.setId(ovlId);
			}

			saveConfig(config);
		}

		return true;
	}

	@Override
	public Config findOneNoSecurity(String id) {
		return findById(id);
	}

	@Override
	public Config findOneNoSecurity(String name, String environment) {
		return configDao.find(name, environment);
	}

	@Override
	public void refreshConfig(String jobId, String taskName) {
		DataTaskMakeUpEntity dataTaskMakeUpEntity = new DataTaskMakeUpEntity();
		dataTaskMakeUpEntity.setTaskId(jobId);
		List<DataTaskMakeUpEntity> dataTaskMakeUpEntitys = getSearchExecutor().searchForList(dataTaskMakeUpEntity,
				DataTaskMakeUpEntity.class);
		List<Map<String, Object>> jobs = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> plugs = new ArrayList<Map<String, Object>>();
		Map<String, Object> coreMap = LMap.createMap(Object.class).pu("channel", new HashMap<String, Object>())
				.pu("exchange", new HashMap<String, Object>()).pu("executor", new HashMap<String, Object>())
				.pu("plugs", plugs);
		Map<String, Object> jobMap = LMap.toMap("core", coreMap, "job", jobs);
		if (dataTaskMakeUpEntitys.size() <= 1) {
			throw new RuntimeException("参数不够缺失配置参数!");
		}
		List<DataTaskMakeUpEntity> plugEntitys = new ArrayList<>();

		for (DataTaskMakeUpEntity dataTaskMakeUpEntity2 : dataTaskMakeUpEntitys) {
			switch (dataTaskMakeUpEntity2.getTypeName()) {
			case "job.input":
				getMap(jobs, dataTaskMakeUpEntity2.getTabIndex() - 1).put("input",
						LMap.toMap("name", dataTaskMakeUpEntity2.getNameValue(), "args",
								JSON.parse(dataTaskMakeUpEntity2.getArgsValue())));
				break;
			case "job.output":
				getMap(jobs, dataTaskMakeUpEntity2.getTabIndex() - 1).put("output",
						LMap.toMap("name", dataTaskMakeUpEntity2.getNameValue(), "args",
								JSON.parse(dataTaskMakeUpEntity2.getArgsValue())));
				break;
			case "channel.output":
				((Map<String, Object>) coreMap.get("channel")).put("output",
						LMap.toMap("name", dataTaskMakeUpEntity2.getNameValue(), "args",
								JSON.parse(dataTaskMakeUpEntity2.getArgsValue())));
				break;
			case "channel.input":
				((Map<String, Object>) coreMap.get("channel")).put("input",
						LMap.toMap("name", dataTaskMakeUpEntity2.getNameValue(), "args",
								JSON.parse(dataTaskMakeUpEntity2.getArgsValue())));
				break;
			case "exchange":
				((Map<String, Object>) coreMap.get("exchange")).put("name", dataTaskMakeUpEntity2.getNameValue());
				((Map<String, Object>) coreMap.get("exchange")).put("args",
						JSON.parse(dataTaskMakeUpEntity2.getArgsValue()));
				break;
			case "executor.input":
				((Map<String, Object>) coreMap.get("executor")).put("input",
						LMap.toMap("name", dataTaskMakeUpEntity2.getNameValue(), "args",
								JSON.parse(dataTaskMakeUpEntity2.getArgsValue())));
				break;
			case "executor.output":
				((Map<String, Object>) coreMap.get("executor")).put("output",
						LMap.toMap("name", dataTaskMakeUpEntity2.getNameValue(), "args",
								JSON.parse(dataTaskMakeUpEntity2.getArgsValue())));
				break;
			case "plugs":
				// 解决一开始多个plug 后续删除第一个plug 产生的plug index没有1 不连续造成索引越界异常
				plugEntitys.add(dataTaskMakeUpEntity2);
				break;
			default:
				break;
			}
		}
		plugEntitys.sort(Comparator.comparingInt(DataTaskMakeUpEntity::getTabIndex));
		int plugCurrentIndex = 1;
		for (DataTaskMakeUpEntity entity : plugEntitys) {
			entity.setTabIndex(plugCurrentIndex++);
		}
		Map<String, Object> plugMap;
		for (DataTaskMakeUpEntity entity : plugEntitys) {
			plugMap = getMap(plugs, entity.getTabIndex() - 1);
			plugMap.put("name", entity.getNameValue());
			plugMap.put("args", JSON.parse(entity.getArgsValue()));

		}

		Config config = new Config();
		config.setId(jobId);
		config.setCreateDate(new Date());
		config.setContent(JSON.toJSONString(jobMap));
		config.setType("json");
		config.setName(taskName);
		config.setDescription(taskName);
		config.setUpdateDate(new Date());
		config.setGroup("job");
		save(config);
	}

	private Map<String, Object> getMap(List<Map<String, Object>> maps, int index) {
		if (maps.size() <= index) {
			for (int i = 0; i < index - maps.size() + 1; i++) {
				maps.add(new HashMap<String, Object>());
			}
		}
		return maps.get(index);
	}

}
