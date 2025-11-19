package net.risesoft.controller.security;

import net.risesoft.api.aop.CheckResult;
import net.risesoft.dto.EnvironmentDTO;
import net.risesoft.security.ConcurrentSecurity;
import net.risesoft.security.SecurityManager;
import net.risesoft.security.model.Environment;
import net.risesoft.security.service.EnvironmentService;
import net.risesoft.pojo.Y9Result;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rest/system/Environment")
public class EnvironmentController {
	
	@Autowired
	private EnvironmentService environmentService;
	
	@Autowired
	private SecurityManager securityManager;
	
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * 获取所有环境根据权限 此权限是安全管理权限
	 *
	 * @return
	 */
	@GetMapping("getAll")
	public Y9Result<List<Environment>> getAll() {
		ConcurrentSecurity concurrentSecurity = securityManager.getConcurrentSecurity();
		if(concurrentSecurity.getEnvironments().size() > 0) {
			return Y9Result.success(environmentService.findForEnvironment(concurrentSecurity.getEnvironments()));
		}
		return Y9Result.success(environmentService.findAll());
	}

	/**
	 * 获取权限这个是根据权限配置的可操作环境
	 *
	 * @return
	 */
	@GetMapping("getEnvironment")
	public Y9Result<List<Environment>> getEnvironment() {
		ConcurrentSecurity concurrentSecurity = securityManager.getConcurrentSecurity();
		return Y9Result.success(environmentService.findForEnvironment(concurrentSecurity.getEnvironments()));
	}

	/**
	 * 新增环境
	 *
	 * @param environment 环境
	 * @param token       token
	 * @return
	 */
	@CheckResult
	@PostMapping("insertEnvironment")
	public Y9Result<Object> insertEnvironment(@RequestBody @Valid EnvironmentDTO environmentDTO, BindingResult result) {
		Environment environment = modelMapper.map(environmentDTO, Environment.class);
		environmentService.insertEnvironment(environment);
		return Y9Result.success(environment.getName());
	}

	/**
	 * 修改环境
	 * 
	 * @param environment
	 * @param result
	 * @return
	 */
	@CheckResult
	@PostMapping("updateEnvironment")
	public Y9Result<Object> updateEnvironment(@Valid EnvironmentDTO environmentDTO, BindingResult result) {
		Environment environment = modelMapper.map(environmentDTO, Environment.class);
		environmentService.updateEnvironment(environment);
		return Y9Result.success(environment.getName());
	}

	/**
	 * 删除权限
	 *
	 * @param id    id
	 * @param token token
	 * @return
	 */
	@PostMapping("deleteEnvironment")
	public Y9Result<Object> deleteEnvironment(@RequestParam(required = true) String id) {
		environmentService.delById(id);
		return Y9Result.success(1);
	}

}
