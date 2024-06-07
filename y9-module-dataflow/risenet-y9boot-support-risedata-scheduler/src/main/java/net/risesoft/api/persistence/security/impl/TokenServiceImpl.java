package net.risesoft.api.persistence.security.impl;

import cn.hutool.core.date.DateUtil;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.api.exceptions.TokenException;
import net.risesoft.api.persistence.dao.TokenDao;
import net.risesoft.api.persistence.model.security.DataUser;
import net.risesoft.api.persistence.model.security.Token;
import net.risesoft.api.persistence.security.TokenService;
import net.risesoft.api.persistence.security.UserService;
import net.risesoft.api.security.SecurityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Description :
 * @ClassName TokenServiceImpl
 * @Author lb
 * @Date 2022/8/3 16:41
 * @Version 1.0
 */
@Service
public class TokenServiceImpl extends AutomaticCrudService<Token, String> implements TokenService {

	@Autowired
	TokenDao tokenDao;

	@Override
	public String createToken(DataUser userModel) {
		Token token = new Token();
		token.setTokenTime(System.currentTimeMillis());
		token.setToken(UUID.randomUUID().toString());
		token.setUserId(userModel.getId());
		insert(token);
		return token.getToken();
	}

	@Autowired
	UserService userService;

	@Override
	public DataUser getUserByToken(String token) {
		String tokenUser = tokenDao.getTokenUser(token);
		if (StringUtils.isEmpty(tokenUser)) {
			throw new TokenException("token 失效或者用户不存在");
		}
		DataUser betaUser = userService.findOne(tokenUser);
		if (betaUser == null) {
			throw new TokenException("token 失效或者用户不存在");
		}
		betaUser.setPassword("");
		return betaUser;
	}

	@Override
	public boolean renew(String token, long time) {
		return tokenDao.renew(token, time) == 1;
	}

	@Override
	public boolean deleteToken(String token, long time) {
		return tokenDao.deleteToken(token, time) == 1;
	}

	@Override
	public long getFailureTime() {
		return System.currentTimeMillis() - FAILURE_TIME * 1000 * 60;
	}

	@Override
	public boolean removeToken(String token) {
		return tokenDao.removeToken(token) == 1;
	}

	/**
	 * 过期时间 单位分钟 默认8小时
	 */
	@Value("${beta.token.time:480}")
	public Long FAILURE_TIME;



	@Override
	public List<String> getFailureToken() {
		return tokenDao.getTokenForTime(getFailureTime());
	}

}
