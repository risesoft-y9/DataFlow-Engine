package net.risesoft.api.persistence.log.impl;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.search.LPageable;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.api.persistence.dao.LogDao;
import net.risesoft.api.persistence.log.LogService;
import net.risesoft.api.persistence.model.log.Log;
import net.risesoft.api.utils.AutoIdUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @Description :
 * @ClassName LogServiceImpl
 * @Author lb
 * @Date 2022/8/17 14:31
 * @Version 1.0
 */
@Service
public class LogServiceImpl extends AutomaticCrudService<Log, String> implements LogService {
    @Override
    public void addLog(Log log, String ip) {
        log.setCreateDate(new Date());
        log.setId(AutoIdUtil.getRandomId26());
        log.setIp(ip);
        insert(log);
    }

    @Override
    public LPage<Log> searchForLog(Log log, LPageable pageable) {
        return searchForPage(log, pageable);
    }

    @Override
    public LPage<Log> searchForLog(Log log, LPageable pageable, Map<String, Operation> context) {
        return searchForPage(log, pageable, null, context);
    }

    @Autowired
    LogDao logDao;

    @Override
    public void clear(Date date) {
        logDao.clear(date);
    }
}
