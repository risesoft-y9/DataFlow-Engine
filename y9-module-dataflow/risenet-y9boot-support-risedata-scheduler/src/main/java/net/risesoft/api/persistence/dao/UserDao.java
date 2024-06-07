package net.risesoft.api.persistence.dao;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;
import net.risesoft.api.persistence.model.security.DataUser;

import java.util.List;

/**
 * @Description :
 * @ClassName UserDao
 * @Author lb
 * @Date 2022/8/3 16:04
 * @Version 1.0
 */
public interface UserDao extends Repository {

    @Search("select * from Y9_DATASERVICE_USER where ACCOUNT = ? and PASSWORD=?")
    DataUser getUser(String account, String password);
    @Search("select USER_NAME from Y9_DATASERVICE_USER")
    List<String> findAll();

    @Search("select count(*) from Y9_DATASERVICE_USER where USER_NAME=?")
    Integer hasName(String name);

    @Modify("update Y9_DATASERVICE_USER set PASSWORD=? where id=?")
    Integer updatePassword(String password, String id);

    @Modify("delete  from  Y9_DATASERVICE_USER  where ID=?")
    Integer deleteUser( String id);

}
