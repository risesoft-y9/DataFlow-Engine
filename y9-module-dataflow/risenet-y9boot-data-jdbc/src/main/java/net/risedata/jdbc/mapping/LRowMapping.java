package net.risedata.jdbc.mapping;

import java.sql.ResultSet;
/**
 * 自定义处理映射
 * @author libo
 *2020年11月30日
 * @param <T>
 */
@FunctionalInterface
public interface LRowMapping <T>{
    void handle(T t,ResultSet set,int index);
}
