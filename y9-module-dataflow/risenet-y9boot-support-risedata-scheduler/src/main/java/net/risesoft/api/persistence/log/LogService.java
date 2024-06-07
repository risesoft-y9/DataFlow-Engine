package net.risesoft.api.persistence.log;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.persistence.model.log.Log;

import java.util.Date;
import java.util.Map;

/**
 * @Description : 日志服务
 * @ClassName LogService
 * @Author lb
 * @Date 2022/8/17 14:30
 * @Version 1.0
 */
public interface LogService {

    /**
     *  添加日志
     * @param log 日志
     * @param ip ip
     */
    void addLog(Log log,String ip);

    /**
     * 查询日志
     * @param log
     * @param pageable
     * @return
     */
     LPage<Log> searchForLog(Log log, LPageable pageable);

    LPage<Log> searchForLog(Log log, LPageable pageable, Map<String, Operation> context);

    void clear(Date date);
}
