package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.job.JobService;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.dto.DataArrangeDTO;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.listener.ArrangeExecuteListener;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataArrangeService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataArrangeEntity;
import net.risesoft.y9public.entity.DataArrangeLogEntity;
import net.risesoft.y9public.entity.DataProcessEntity;
import net.risesoft.y9public.repository.DataArrangeLogRepository;
import net.risesoft.y9public.repository.DataArrangeRepository;
import net.risesoft.y9public.repository.DataProcessRepository;
import net.risesoft.y9public.repository.spec.DataArrangeSpecification;

@Service(value = "dataArrangeService")
@RequiredArgsConstructor
public class DataArrangeServiceImpl implements DataArrangeService {
	
	private final DataArrangeRepository dataArrangeRepository;
	private final DataProcessRepository dataProcessRepository;
	private final JobLogService jobLogService;
	private final DataArrangeLogRepository dataArrangeLogRepository;
	private final ArrangeExecuteListener arrangeExecuteListener;
	private final JobService jobService;
	private final ModelMapper modelMapper;
	
	@Override
	public Page<DataArrangeEntity> searchPage(String name, Integer pattern, int page, int rows) {
		if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "createTime"));
        DataArrangeSpecification spec = new DataArrangeSpecification(name, pattern, Y9LoginUserHolder.getTenantId());
		return dataArrangeRepository.findAll(spec, pageable);
	}

	@Override
	public DataArrangeEntity getById(String id) {
		return dataArrangeRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> deleteData(String id) {
		if(StringUtils.isNotBlank(id)) {
			dataArrangeRepository.deleteById(id);
			dataProcessRepository.deleteByArrangeId(id);
		}else {
			return Y9Result.failure("id不能为空");
		}
		return Y9Result.successMsg("删除成功");
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> saveData(DataArrangeDTO arrangeDTO) {
		try {
			DataArrangeEntity entity = modelMapper.map(arrangeDTO, DataArrangeEntity.class);
			if (StringUtils.isBlank(entity.getId())) {
				entity.setId(Y9IdGenerator.genId());
			}else {
				DataArrangeEntity dataArrangeEntity = dataArrangeRepository.findById(entity.getId()).orElse(null);
				if(dataArrangeEntity != null) {
					entity.setXmlData(dataArrangeEntity.getXmlData());
				}
			}
			entity.setUserId(Y9LoginUserHolder.getPersonId());
			entity.setUserName(Y9LoginUserHolder.getUserInfo().getName());
			entity.setTenantId(Y9LoginUserHolder.getTenantId());
			dataArrangeRepository.save(entity);
			
			//保存定时调度信息
			if(StringUtils.isNotBlank(entity.getCron())) {
				Job job = jobService.findByArgsAndTypeAndEnvironmentAndServiceId(entity.getId(), "local", "Public", "RISEDATA-MASTER");
				if(job == null) {
					job = new Job();
				}
				job.setArgs(entity.getId());
				job.setBlockingStrategy("串行");
				job.setDispatchMethod("均衡");
				job.setDispatchType("cron");
				job.setDescription(entity.getContent());
				job.setEnvironment("Public");
				job.setErrorCount(0);
				job.setJobSource("任务编排");
				job.setJobType(entity.getUserId());
				job.setName(entity.getName());
				job.setServiceId("RISEDATA-MASTER");
				job.setSource("dataArrangeService,executeProcess");
				job.setSourceTimeOut(1200);
				job.setSpeed(entity.getCron());
				job.setStatus(entity.getPattern());
				job.setTimeOut(3600);
				job.setType("local");
				jobService.saveJob(job);
			}else {
				// 删除定时任务
				Job job = jobService.findByArgsAndTypeAndEnvironmentAndServiceId(entity.getId(), "local", "Public", "RISEDATA-MASTER");
				if(job != null) {
					jobService.deleteByJobId(job.getId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("保存失败：" + e.getMessage());
		}
		return Y9Result.successMsg("保存成功");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> saveXml(String id, String xmlData, String parseData) {
		DataArrangeEntity info = getById(id);
		if(info != null) {
			info.setXmlData(xmlData);
			dataArrangeRepository.save(info);
			
			// 保存节点信息
			List<List> dataList = Y9JsonUtil.readList(parseData, List.class);
			int step = 1;
			for(List<Map<String, Object>> list : dataList) {
				DataProcessEntity dataProcessEntity = new DataProcessEntity();
				dataProcessEntity.setId(Y9IdGenerator.genId());
				dataProcessEntity.setStep(step);
				dataProcessEntity.setArrangeId(id);
				List<Map<String, Object>> jobIds = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> subJobs = new ArrayList<Map<String,Object>>();
				for(Map<String, Object> map : list) {
					if(dataProcessEntity.getGateway() == null) {
						dataProcessEntity.setGateway((Integer)map.get("gateway"));
					}
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("name", map.get("name"));
					data.put("taskId", map.get("taskId"));
					data.put("executNode", map.get("executNode"));
					jobIds.add(data);
					
					if(map.get("subJobs") != null) {
						subJobs.addAll((List<Map<String, Object>>) map.get("subJobs"));
					}
				}
				dataProcessEntity.setJobIds(Y9JsonUtil.writeValueAsString(jobIds));
				dataProcessEntity.setSubJobs(Y9JsonUtil.writeValueAsString(subJobs));
				dataProcessRepository.save(dataProcessEntity);
				step++;
			}
		}else {
			return Y9Result.failure("保存失败，主体数据不存在");
		}
		return Y9Result.successMsg("保存成功");
	}

	@Override
	public Y9Result<String> executeProcess(String id) {
		DataArrangeEntity info = getById(id);
		if(info != null) {
			List<DataProcessEntity> processList = dataProcessRepository.findByArrangeIdOrderByStep(id);
			if(processList == null || processList.size() == 0) {
				return Y9Result.failure("执行失败，任务流程不存在");
			}
			Integer identifier = dataArrangeLogRepository.getMaxIdentifier(id);
			if(identifier == null) {
				identifier = 1;
			}else {
				identifier = identifier + 1;
			}
			// 执行任务
			arrangeExecuteListener.executeJob(id, processList, identifier);
		}else {
			return Y9Result.failure("执行失败，主体数据不存在");
		}
		return Y9Result.successMsg("执行发送成功");
	}

	@Override
	public Y9Page<Map<String, Object>> getLogList(String id, int page, int rows) {
		if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "identifier", "createTime"));
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
		Page<DataArrangeLogEntity> pageList = dataArrangeLogRepository.findByArrangeId(id, pageable);
		for(DataArrangeLogEntity log : pageList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("createTime", log.getCreateTime());
			map.put("identifier", log.getIdentifier());
			map.put("jobName", log.getJobName());
			if(StringUtils.isNotBlank(log.getJobLogId())) {
				JobLog jobLog = jobLogService.findById(log.getJobLogId());
				if(jobLog != null) {
					map.put("logId", log.getJobLogId());
					map.put("message", jobLog.getLogConsole());
				}else {
					map.put("message", "日志不存在，或已清除");
					map.put("logId", "");
				}
			}else {
				map.put("message", log.getErrorMsg());
				map.put("logId", "");
			}
			listMap.add(map);
		}
		return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), listMap, "获取数据成功");
	}

}