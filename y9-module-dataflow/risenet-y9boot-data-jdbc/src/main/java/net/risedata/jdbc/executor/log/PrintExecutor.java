package net.risedata.jdbc.executor.log;

/**
 * 用于打印操作 会将sql 传入到此接口的实现类中进行进行输出到控制台
 * 
 * @author libo 2021年2月18日
 */
public interface PrintExecutor {
	void print(String msg, Object[] objects);

}
