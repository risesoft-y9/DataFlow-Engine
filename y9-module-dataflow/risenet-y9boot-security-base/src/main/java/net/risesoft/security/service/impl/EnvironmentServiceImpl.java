package net.risesoft.security.service.impl;

import net.risedata.jdbc.factory.OperationBuilderFactory;
import net.risedata.jdbc.operation.impl.InOperation;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.exceptions.ServiceOperationException;
import net.risesoft.util.AutoIdUtil;
import net.risesoft.security.dao.CommonDao;
import net.risesoft.security.dao.EnvironmentDao;
import net.risesoft.security.model.Environment;
import net.risesoft.security.service.EnvironmentService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvironmentServiceImpl extends AutomaticCrudService<Environment, String> implements EnvironmentService {
	
	public static final String PUBLIC = "Public";

	@Autowired
	EnvironmentDao environmentDao;

	@Autowired
	CommonDao commonDao;

	@Override
	public List<Environment> findAll() {
		return searchAll();
	}

	@Override
	public void insertEnvironment(Environment role) {
		if (environmentDao.hasByName(role.getName(), "null") > 0) {
			throw new ServiceOperationException("已存在: " + role.getName());
		}
		role.setId(AutoIdUtil.getRandomId26());
		save(role);
	}

	@Override
	public void delById(String id) {
		if (PUBLIC.equals(id)) {
			throw new ServiceOperationException("默认环境不能删除");
		}
		deleteById(id);
	}

	@Override
	public String getEnvironmentById(String environment) {
		String name = environmentDao.findById(environment);
		if (StringUtils.isEmpty(name)) {
			throw new ServiceOperationException("没有找到该环境" + environment);
		}
		return name;
	}

	@Override
	public String getEnvironmentByName(String environment) {
		String id = environmentDao.findByName(environment);
		if (StringUtils.isEmpty(id)) {
			throw new ServiceOperationException("没有找到该环境" + environment);
		}
		return id;
	}

	@Override
	public List<String> findNameByIds(List<String> environments) {
		return commonDao.findNameByIds(Environment.class, environments);
	}

	@Override
	public List<Environment> findForEnvironment(List<String> environments) {
		if(environments.size() == 0) {
			return null;
		}
		return search("*", null, OperationBuilderFactory.builder("id", new InOperation(true, environments)));
	}

	@Override
	public synchronized void updateEnvironment(Environment environment) {
		if (environmentDao.hasByName(environment.getName(), environment.getId()) > 0) {
			throw new ServiceOperationException("已存在: " + environment.getName());
		}
		environmentDao.updateEnvironment(environment.getName(), environment.getDescription(), environment.getId());
	}
}
