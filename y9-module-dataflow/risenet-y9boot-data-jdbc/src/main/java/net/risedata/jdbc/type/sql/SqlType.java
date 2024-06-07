package net.risedata.jdbc.type.sql;
/**
 * java 类型与 sql类型的对应
 * @author libo
 *2021年2月8日
 */
public interface SqlType {
    boolean isHandle(String type);
    Class<?> getType(String type);
}
