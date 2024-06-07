package net.risesoft.controller.security;

import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.aop.CheckHttpForArgs;
import net.risesoft.api.aop.CheckResult;
import net.risesoft.api.persistence.model.security.NetworkWhiteList;
import net.risesoft.api.persistence.security.NetworkWhiteListService;
import net.risesoft.pojo.Y9Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description : rpc 服务注册/获取白名单
 * @ClassName NetworkWhiteListController
 * @Author lb
 * @Date 2022/8/8 10:39
 * @Version 1.0
 */
@RestController()
@RequestMapping("/api/rest/system/networkWhiteList")
public class NetworkWhiteListController {

	@Autowired
	NetworkWhiteListService networkWhiteListService;

	/**
	 * 保存/修改
	 *
	 * @param networkWhiteList
	 * @return
	 */
	@PostMapping("save")
	@CheckResult
	@CheckHttpForArgs
	public Y9Result<Object> save(@Valid NetworkWhiteList networkWhiteList, BindingResult result) {
		networkWhiteListService.saveNetworkWhiteList(networkWhiteList);
		return Y9Result.success("ok");
	}

	/**
	 * 根据id 删除
	 *
	 * @param id
	 * @return
	 */
	@PostMapping("delById")
	public Y9Result<Object> delById(String id) {
		networkWhiteListService.delById(id);
		return Y9Result.success("ok");
	}

	/**
	 * 查询 此处需要判断查询条件类
	 *
	 * @param networkWhiteList
	 * @param pageable
	 * @return
	 */
	@GetMapping("searchForPage")
	@CheckHttpForArgs
	public Y9Result<Object> searchForPage(NetworkWhiteList networkWhiteList, LPageable pageable) {
		return Y9Result.success(networkWhiteListService.searchByNetworkWhiteList(networkWhiteList, pageable));
	}
	
	/**
	 * 根据id 删除
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("findById")
	public Y9Result<Object> findById(String id) {
		return Y9Result.success(networkWhiteListService.findById(id));
	}
}
