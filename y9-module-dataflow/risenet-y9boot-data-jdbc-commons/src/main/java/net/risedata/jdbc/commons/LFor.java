package net.risedata.jdbc.commons;
/**
 * ford 接口
 * @author libo
 *2020年6月16日
 */
public interface LFor<T> {
 public void invoke(T t,int index);
}
