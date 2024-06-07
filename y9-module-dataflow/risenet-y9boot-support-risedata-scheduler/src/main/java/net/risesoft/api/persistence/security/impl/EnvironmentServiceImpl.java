package net.risesoft.api.persistence.security.impl;

import net.risedata.jdbc.factory.OperationBuilderFactory;
import net.risedata.jdbc.operation.impl.InOperation;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.api.exceptions.ServiceOperationException;
import net.risesoft.api.persistence.dao.CommonDao;
import net.risesoft.api.persistence.dao.EnvironmentDao;
import net.risesoft.api.persistence.model.security.Environment;
import net.risesoft.api.persistence.security.EnvironmentService;
import net.risesoft.api.utils.AutoIdUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Description :
 * @ClassName EnvironmentServiceImpl
 * @Author lb
 * @Date 2022/8/4 16:58
 * @Version 1.0
 */
@Service
public class EnvironmentServiceImpl extends AutomaticCrudService<Environment, String>
		implements EnvironmentService, ApplicationListener<ApplicationStartedEvent> {
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
		String name = environmentDao.findByID(environment);
		if (StringUtils.isEmpty(name)) {
			throw new ServiceOperationException("没有找到该环境" + environment);
		}
		return name;
	}

	@Override
	public String getEnvironmentByName(String environment) {
		String id = environmentDao.findByNAME(environment);
		if (StringUtils.isEmpty(id)) {
			throw new ServiceOperationException("没有找到该环境" + environment);
		}
		return id;
	}

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		if (environmentDao.hasPulibc() < 1) {
			Environment environment = new Environment();
			environment.setDescription("默认环境");
			environment.setName(PUBLIC);
			environment.setId("Public");
			insert(environment);
			environment = new Environment();
			environment.setDescription("测试环境");
			environment.setName("dev");
			environment.setId("dev");
			insert(environment);
		}
	}

	@Override
	public List<String> findNameByIds(List<String> environments) {
		return commonDao.findNameByIds(Environment.class, environments);
	}

	@Override
	public List<Environment> findForEnvironment(List<String> environments) {
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
