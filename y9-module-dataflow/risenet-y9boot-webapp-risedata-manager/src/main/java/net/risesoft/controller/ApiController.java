package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.entity.DataTaskEntity;
import net.risesoft.y9public.repository.DataTaskRepository;

@Validated
@RestController
@RequestMapping(value = "/services/rest", produces = "application/json")
@RequiredArgsConstructor
public class ApiController {
	
	private final DataTaskRepository dataTaskRepository;
	
	@Resource(name = "jdbcTemplate4Public")
	private JdbcTemplate jdbcTemplate4Public;
	
	@GetMapping(value = "/getTestData")
    public Y9Page<Map<String, Object>> get(int num, int page, int size) {
		List<Map<String, Object>> listMap = jdbcTemplate4Public.queryForList("select * from test_data where id <= " 
				+ num + " ORDER BY id LIMIT ? OFFSET ?", size, page * size);
        return Y9Page.success(page, (int) Math.ceil((double) num / size), num, listMap);
    }
	
//	@PostMapping(value = "/saveTestData")
//	public Y9Result<String> save(String id, String name, Integer sex, String mobile) {
//		DataSource dataSource = dataSourceService.getDataSource("1652015345457696768");
//		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//		int count = jdbcTemplate.update("UPDATE USER_DATA SET NAME = ?, SEX = ?, MOBILE = ? WHERE id = ?", name, sex, mobile, id);
//		if(count <= 0) {
//			jdbcTemplate.update("INSERT INTO USER_DATA (id, name, sex, mobile) VALUES (?, ?, ?, ?)", id, name, sex, mobile);
//		}
//		return Y9Result.successMsg("保存成功");
//	}
	
	@GetMapping(value = "/getDataFlowLog")
	public Y9Page<Map<String, Object>> getDataFlowLog(String systemName, String tenantId, int page, int size) {
		try {
			List<String> jobIds = new ArrayList<String>();// 调度ID
			// 获取系统的相关配置任务ID
			List<String> idList = dataTaskRepository.findBySystemNameAndTenantId(systemName, tenantId);
			for(String id : idList) {
				// 获取配置任务的调用任务
				List<Map<String, Object>> listMap = jdbcTemplate4Public.queryForList("select * from Y9_DATASERVICE_JOB where ARGS like ?", id + "%");
				for(Map<String, Object> dataMap : listMap) {
					jobIds.add(dataMap.get("ID").toString());
				}
			}
			// 手动拼接IN后的占位符：根据集合长度生成 "?, ?, ..."
	        String placeholders = jobIds.stream()
	                .map(id -> "?")
	                .collect(Collectors.joining(", "));

	        // 拼接完整SQL
	        String sql = String.format("select COUNT(*) from Y9_DATASERVICE_JOB_LOG where JOB_ID IN (%s)", placeholders);
			// 获取相关调度任务日志
			Integer count = jdbcTemplate4Public.queryForObject(sql, Integer.class, jobIds.toArray());
			if(count == 0) {
				return Y9Page.success(page, 0, 0, new ArrayList<>()); 
			}
			String sql2 = String.format("select * from Y9_DATASERVICE_JOB_LOG where JOB_ID in (%s) ORDER BY ID "
					+ "LIMIT %s OFFSET %s", placeholders, size, (page - 1) * size);
			List<Map<String, Object>> listMap = jdbcTemplate4Public.queryForList(sql2, jobIds.toArray());
			for(Map<String, Object> map : listMap) {
				// 获取调度任务ID
				String jobId = map.get("JOB_ID").toString();
				// 获取配置任务ID
				String taskId = jdbcTemplate4Public.queryForObject("select p.ARGS from Y9_DATASERVICE_JOB p where ID = " + jobId, String.class);
				if(StringUtils.isNotBlank(taskId)) {
					String[] ids = taskId.split(",");
					DataTaskEntity dataTaskEntity = dataTaskRepository.findById(ids[0]).orElse(null);
					if(dataTaskEntity != null) {
						map.put("EXTERNALID", dataTaskEntity.getExternalId());
					} else {
						map.put("EXTERNALID", "");
					}
				} else {
					map.put("EXTERNALID", "");
				}
			}
			return Y9Page.success(page, (int) Math.ceil((double) count / size), count, listMap);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return Y9Page.failure(0, 0, 0, null, "程序出错：" + e.getMessage(), 500);
		}
	}

}
