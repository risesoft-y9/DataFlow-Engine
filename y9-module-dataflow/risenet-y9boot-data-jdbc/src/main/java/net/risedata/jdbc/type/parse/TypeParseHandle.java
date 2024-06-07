package net.risedata.jdbc.type.parse;
/**
 * 用来解析类型标签的操作由
 * @author libo
 *2020年11月30日
 */
public interface TypeParseHandle {
    boolean isHandle(String type);
    String parseValue(String value,String type); 
}
