package net.risesoft.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.entity.DataLogEntity;
import net.risesoft.y9public.repository.DataLogRepository;

@Validated
@RestController
@RequestMapping(value = "/api/rest", produces = "application/json")
@RequiredArgsConstructor
public class DataLogController {

	private final DataLogRepository dataLogRepository;

	@GetMapping(value = "/get")
	public Y9Page<DataLogEntity> get(String jobId, Integer page, Integer size) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
		Page<DataLogEntity> pageList = dataLogRepository.findByJobId(jobId, pageable);
		return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(), "获取数据成功");
	}

}
