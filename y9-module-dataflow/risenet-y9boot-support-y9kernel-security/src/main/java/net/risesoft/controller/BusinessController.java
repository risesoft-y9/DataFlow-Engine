package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataBusinessService;

@Validated
@RestController
@RequestMapping(value = "/api/rest/business", produces = "application/json")
@RequiredArgsConstructor
public class BusinessController {
	
	private final DataBusinessService dataBusinessService;

	/**
	 * 获取业务分类列表
	 * @return
	 */
	@GetMapping("/findAll")
	public Y9Result<List<Map<String, Object>>> findAll() {
        return Y9Result.success(dataBusinessService.findAll(), "获取数据成功");
    }
	
}
