package net.risesoft.controller;

import org.springframework.beans.factory.annotation.Autowired;

import net.risesoft.exceptions.ServiceOperationException;
import net.risesoft.security.ConcurrentSecurity;
import net.risesoft.security.SecurityManager;

public class BaseController {

	@Autowired
	private SecurityManager securityManager;

	/**
	 * 判断环境权限
	 * @param environment
	 * @return
	 */
	public ConcurrentSecurity getSecurityJurisdiction(String environment) {
		ConcurrentSecurity jurisdiction = securityManager.getConcurrentSecurity();
		
		if (jurisdiction.getEnvironments().size() == 0 || jurisdiction.getEnvironments().indexOf(environment) > -1) {
			return jurisdiction;
		}
		throw new ServiceOperationException("no permission");
	}

}
