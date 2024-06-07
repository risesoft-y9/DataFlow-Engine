package net.risesoft.api.persistence.dao;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;
import net.risesoft.api.persistence.model.config.Config;

import java.util.Date;
import java.util.List;

/**
 * @Description : 配置文件操作
 * @ClassName ConfigHisDao
 * @Author lb
 * @Date 2022/8/3 16:04
 * @Version 1.0
 */
public interface ConfigDao extends Repository {


    @Search("select ID from Y9_DATASERVICE_CONFIG where NAME =? and GROUP_NAME=? and ENVIRONMENT=? ")
    String find(String name, String group, String environment);

    @Search("select * from Y9_DATASERVICE_CONFIG where NAME =?  and ENVIRONMENT=? ")
    Config find(String name, String environment);

    @Search("select * from Y9_DATASERVICE_CONFIG where  GROUP_NAME in (?1) and NAME in (?2) and ENVIRONMENT=?3  ")
    List<Config> findAll(String[] groups, String[] configs, String environment);
    @Search("select id from Y9_DATASERVICE_CONFIG where  GROUP_NAME in (?1) and NAME in (?2) and ENVIRONMENT=?3  ")
    List<String> findIdAll(String[] groups, String[] configs, String environment);
}
