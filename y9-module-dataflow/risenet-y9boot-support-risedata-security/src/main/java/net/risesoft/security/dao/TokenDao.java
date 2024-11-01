package net.risesoft.security.dao;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;

import java.util.List;

/**
 * @Description :
 * @ClassName TokenDao
 * @Author lb
 * @Date 2022/8/3 16:04
 * @Version 1.0
 */
public interface TokenDao extends Repository {

    @Search("select USER_ID from Y9_DATASERVICE_TOKEN where TOKEN=? ")
    String getTokenUser(String token);

    @Search("select TOKEN from Y9_DATASERVICE_TOKEN where TOKEN_TIME<?  ")
    List<String> getTokenForTime(Long time);
    
    @Modify("delete from Y9_DATASERVICE_TOKEN  where TOKEN=? and TOKEN_TIME <= ?")
    Integer deleteToken(String token, Long time);
    
    @Modify("update  Y9_DATASERVICE_TOKEN  set TOKEN_TIME = ?2  where TOKEN=?1 ")
    Integer renew(String token, Long time);
    
    @Modify("delete from Y9_DATASERVICE_TOKEN  where TOKEN=? ")
    Integer removeToken(String token);
}
