package net.risesoft.security.service.impl;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.factory.OperationBuilderFactory;
import net.risedata.jdbc.operation.impl.CustomOperation;
import net.risedata.jdbc.search.LPageable;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.exceptions.ServiceOperationException;
import net.risesoft.security.dao.UserDao;
import net.risesoft.security.model.DataUser;
import net.risesoft.security.service.UserService;
import net.risesoft.util.AutoIdUtil;
import net.risesoft.util.MD5Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class UserServiceImpl extends AutomaticCrudService<DataUser, String> implements UserService {
	
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
		DataUser dataUser = findOne(userId);
		if(dataUser != null && dataUser.getUserName().equals("admin")) {
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

	@Override
	public Integer hasName(String name) {
		return userDao.hasName(name);
	}

	@Override
	public DataUser getByLoginName(String account) {
		return userDao.getUser(account);
	}
}
