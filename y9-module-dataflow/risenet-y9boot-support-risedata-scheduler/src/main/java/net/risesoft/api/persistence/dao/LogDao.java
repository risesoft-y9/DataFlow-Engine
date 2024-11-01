package net.risesoft.api.persistence.dao;

import java.util.Date;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.repository.Repository;

/**
 * @Description : 历史配置文件操作
 * @ClassName ConfigHisDao
 * @Author lb
 * @Date 2022/8/3 16:04
 * @Version 1.0
 */
public interface LogDao extends Repository {

    @Modify("delete from Y9_DATASERVICE_JOB where CREATE_DATE < ?")
    Integer clear(Date date);

}
