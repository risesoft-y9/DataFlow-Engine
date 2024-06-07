package net.risedata.jdbc.commons.service;

public class Empty {
  /**	
   * 判断 一个对象是否为空 不为空调用 noEmpty方法 为空调用 empty方法
   * @param obj
   * @param noEmpty
   * @param empty
   */
  public static void isEmpty(IsEmpty obj,Invoke noEmpty,Invoke empty) {
	if (obj.isEmpty()) {
		if (empty!=null) {
			empty.run();
		} 
	}else {
		if (noEmpty!=null) {
			noEmpty.run();
		}
	
	}
  }
  public static void isEmpty(IsEmpty obj,Invoke noEmpty) {
	  isEmpty(obj, noEmpty, null);
  }
  /**
   * 判断非空为空返回默认值
   * @param <T>
   * @param value
   * @param defaultValue
   * @param type
   * @return
   */
  public static <T> T nullReturn(T value,T defaultValue) {
	  if (value ==null) {
		return defaultValue;
	 }
	 return value;
  }
}
