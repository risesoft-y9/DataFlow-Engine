package net.risedata.jdbc.mapping;
/**
 * 强转映射操作类
 * @author libo
 *2021年2月8日
 * @param <T>
 */
public interface CastHandleMapping<T> {
     /**
      * 是否被改处理器处理
      * @param type
      * @return
      */
	boolean isHandle(Class<?> type);
     /**
      * 强转
      * @param o
      * @return
      */
     T toValue(Object o);
}
