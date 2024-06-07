package net.risedata.jdbc.executor.set;
/**
 * 用来设置一个对象的值
 * @author libo
 *2021年2月8日
 */
public interface SetValueExecutor<T> {
	/**
	 * 设置一个对象的值
	 * @param o 设置的对象
	 * @param value 设置的值
	 * @param args 其他的参数 交由 根据实现类来实现
	 */
     void setValue(Object o,Object value,T args);
}
