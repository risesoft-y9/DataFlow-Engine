package net.risesoft.controller;

import org.springframework.beans.factory.annotation.Autowired;

import net.risesoft.api.exceptions.ServiceOperationException;
import net.risesoft.api.security.ConcurrentSecurity;
import net.risesoft.api.security.SecurityManager;

/**
 * @Description :
 * @ClassName BaseController
 * @Author lb
 * @Date 2022/8/30 17:55
 * @Version 1.0
 */
public class BaseController {

	@Autowired
	public SecurityManager securityManager;

	public ConcurrentSecurity getSecurityJurisdiction(String environment) {
		ConcurrentSecurity jurisdiction = securityManager.getConcurrentSecurity();
		
		if (jurisdiction.getEnvironments().size()==0||jurisdiction.getEnvironments().indexOf(environment) > -1) {
			return jurisdiction;
		}
		throw new ServiceOperationException("no security");
	}

}
