package net.risedata.jdbc.condition;

import java.util.Map;

/**
 *条件表达式接口
 * @author libo
 *2020年10月19日
 */
public interface Condition {
	/**
	 * 是否执行调用
	 * @param o
	 * @return
	 */
     boolean isHandle(Map<String, Object> o);
}
