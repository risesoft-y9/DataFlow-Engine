package net.risesoft.api.persistence.dao;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;
import net.risesoft.api.persistence.model.security.DataUser;

import java.util.Date;
import java.util.List;

/**
 * @Description : 历史配置文件操作
 * @ClassName ConfigHisDao
 * @Author lb
 * @Date 2022/8/3 16:04
 * @Version 1.0
 */
public interface ConfigHisDao extends Repository {




    @Modify("delete from Y9_DATASERVICE_CONFIG_HIS where UPDATETIME < ?")
    Integer clear(Date date);



}
