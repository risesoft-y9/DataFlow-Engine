package net.risesoft.api.persistence.config.impl;


import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.search.LPageable;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.api.persistence.config.ConfigHisService;
import net.risesoft.api.persistence.dao.ConfigHisDao;
import net.risesoft.api.persistence.log.LogService;
import net.risesoft.api.persistence.model.config.Config;
import net.risesoft.api.persistence.model.config.ConfigHis;
import net.risesoft.api.security.SecurityManager;
import net.risesoft.api.utils.AutoIdUtil;
import net.risesoft.api.utils.SqlUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @Description :
 * @ClassName ConfigHisServiceImpl
 * @Author lb
 * @Date 2022/8/5 10:33
 * @Version 1.0
 */
@Service

public class ConfigHisServiceImpl extends AutomaticCrudService<ConfigHis, String> implements ConfigHisService {


    private Logger logger = LoggerFactory.getLogger(ConfigHisServiceImpl.class);

    @Autowired
    SecurityManager securityManager;

    @Override
    public void saveConfig(Config config, String operation) {

        ConfigHis his = new ConfigHis();
        his.setId(AutoIdUtil.getRandomId26());
        his.setConfigId(config.getId());
        his.setContent(config.getContent());
        his.setName(config.getName());
        his.setUpdateTime(new Date());
        his.setType(config.getType());
        his.setOpName(securityManager.getConcurrentSecurity().getUser().getUserName());

        his.setEnvironment(config.getEnvironment());
        his.setGroup(config.getGroup());
        his.setOpType(operation);
        insert(his);
    }

    @Override
    public LPage<Map<String,Object>> search(ConfigHis config, LPageable pageable) {

        return   getSearchExecutor().searchForPage(config,"ID id,NAME name,TYPE config_type,GROUP_NAME config_group,ENVIRONMENT environment,UPDATETIME updateTime,OP_NAME opName ,OP_TYPE opType,CONFIG_ID configId"
                ,pageable);
    }

    @Override
    public void delConfigById(String id) {
        deleteById(id);
    }

    @Override
    public ConfigHis findOne(String id) {
        return getOne(id);
    }

    @Autowired
    ConfigHisDao configHisDao;

    private static final long HIS_DAY_TIME = 360 * 1000 * 60 * 60 * 24L;

    @Autowired
    LogService logService;

    @Override
    @Scheduled(cron = "0 0 0 * * ? ")
    public void clearHis() {
        configHisDao.clear(new Date(System.currentTimeMillis() - HIS_DAY_TIME));
        logService.clear(new Date(System.currentTimeMillis() - HIS_DAY_TIME));
    }

}
