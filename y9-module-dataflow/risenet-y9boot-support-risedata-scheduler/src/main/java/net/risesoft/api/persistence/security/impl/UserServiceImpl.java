package net.risesoft.api.persistence.security.impl;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.factory.ObjectBuilderFactory;
import net.risedata.jdbc.factory.OperationBuilderFactory;
import net.risedata.jdbc.operation.impl.CustomOperation;
import net.risedata.jdbc.operation.impl.InOperation;
import net.risedata.jdbc.search.LPageable;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.api.exceptions.ServiceOperationException;
import net.risesoft.api.persistence.dao.UserDao;
import net.risesoft.api.persistence.model.security.DataUser;
import net.risesoft.api.persistence.model.security.RoleUserLink;
import net.risesoft.api.persistence.security.RoleLinkService;
import net.risesoft.api.persistence.security.TokenService;
import net.risesoft.api.persistence.security.UserService;
import net.risesoft.api.utils.AutoIdUtil;
import net.risesoft.api.utils.MD5Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description :
 * @ClassName UserServiceImpl
 * @Author lb
 * @Date 2022/8/3 16:01
 * @Version 1.0
 */
@Service
public class UserServiceImpl extends AutomaticCrudService<DataUser, String>
		implements UserService, ApplicationListener<ApplicationStartedEvent> {
	@Autowired
	UserDao userDao;

	@Override
	public LPage<DataUser> searchForPage(DataUser entiry, LPageable page) {
		return super.getSearchExecutor().searchForPage(entiry, "ID,USER_NAME,ACCOUNT,CREATE_DATE", page, null, null,
				null, DataUser.class);
	}

	@Override
	public DataUser checkLogin(String account, String password) {
		DataUser user = userDao.getUser(account, MD5Util.md5(password));
		if (user == null) {
			throw new ServiceOperationException("用户账号或者密码错误");
		}
		return user;
	}

	@Override
	public String createUser(DataUser dataUser) {
		DataUser user = new DataUser();
		user.setAccount(dataUser.getAccount());
		if (search(user).size() > 0) {
			throw new RuntimeException("账号存在!");
		}
		user.setId(AutoIdUtil.getRandomId26());
		user.setUserName(dataUser.getUserName());
		user.setPassword(MD5Util.md5(dataUser.getPassword()));
		user.setCreateDate(new Date());
		insert(user);
		return user.getId();
	}

	@Override
	public void updatePassword(String userId, String password) {
		userDao.updatePassword(MD5Util.md5(password), userId);
	}

	@Override
	public Integer deleteUser(String userId) {
		if (UserServiceImpl.DEFAULT_USER_ACCOUNT.equals(userId)) {
			throw new ServiceOperationException("系统管理员无法删除");
		}
		return userDao.deleteUser(userId);
	}

	@Override
	public List<String> findAll() {
		return userDao.findAll();
	}

	@Override
	public DataUser findOne(String id) {
		return getOne(id);
	}

	public static final String DEFAULT_USER = "admin";
	public static final String DEFAULT_USER_PASSWORD = "admin";
	public static final String DEFAULT_USER_ACCOUNT = "admin";

	@Autowired
	RoleLinkService roleLinkService;

	@Transactional
	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		if (userDao.hasName(DEFAULT_USER) == 0) {
			DataUser user = new DataUser();
			user.setAccount(DEFAULT_USER_ACCOUNT);
			user.setPassword(DEFAULT_USER_PASSWORD);
			user.setUserName(DEFAULT_USER);
			roleLinkService.save(ObjectBuilderFactory.builder(RoleUserLink.class, "roleId", RoleServiceImpl.ADMIN_ID)
					.builder("userId", createUser(user)));
		}
	}

	@Override
	public Integer updateInfoById(DataUser user) {
		user.setPassword(null);
		DataUser betaUser = getOne(user.getId());
		if (betaUser == null) {
			throw new ServiceOperationException("用户不存在!");
		}
		return updateById(user);
	}

	@Override
	public LPage<DataUser> searchForPageRole(DataUser betaUser, LPageable page, String roleId, boolean isNot) {

		return searchForPage(betaUser, page, null,
				OperationBuilderFactory.builder("id", new CustomOperation((where) -> {
					where.append("id " + (isNot ? "not" : "")
							+ " in(select user_id from Y9_DATASERVICE_ROLE_USER_LINK where role_id =?)");
					where.add(roleId);
					return true;
				})));
	}
}
