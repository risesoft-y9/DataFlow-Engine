package net.risesoft.controller.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.persistence.job.JobInfoService;
import net.risesoft.controller.BaseController;
import net.risesoft.pojo.Y9Result;

/**
 * @Description : 任务调度统计信息控制器
 * @ClassName JobInfoController
 * @Author lb
 * @Date 2022/8/30 15:57
 * @Version 1.0
 */
@RestController()
@RequestMapping("/api/rest/job/info")
public class JobInfoController extends BaseController {

	@Autowired
	JobInfoService jobInfoService;

	/**
	 * 根据id 获取任务信息
	 *
	 * @return
	 */
	@GetMapping("getCount")
	public Y9Result<Object> getCount(String environment) {
		return Y9Result.success(jobInfoService.getCount(environment));
	}

	/**
	 * search
	 *
	 * @return
	 */
	@GetMapping("search")
	public Y9Result<Object> search(String startTime, String endTime, String environment) {
		return Y9Result.success(jobInfoService.getInfo(environment, startTime, endTime));
	}

}
