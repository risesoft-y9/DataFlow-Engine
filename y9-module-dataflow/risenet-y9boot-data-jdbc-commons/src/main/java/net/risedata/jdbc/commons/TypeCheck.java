package net.risedata.jdbc.commons;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * 检测类型
 * @author libo
 *2020年8月18日
 */
public class TypeCheck {
  public static boolean isBaseType(Object o) {//是否为基础类型
	  return isBaseTypeClass(o.getClass());
  }
  public static boolean isBaseTypeClass(Class<?> o) {//是否为基础类型  
	  if( o == Boolean.class||o == Integer.class||o == Double.class||o == Byte.class || o == String.class
			  || o == Short.class || o == Long.class||o == Float.class||o==Double.class
			||o.isPrimitive()||o.isArray()||Iterable.class.isAssignableFrom(o)) {
		  return true;
	  }else {	  
		 return false;  
	  }
  }
  
  public static boolean isBooleanClass(Class<?> o) {
	
	  return o == Boolean.class||o==boolean.class;
  }
  /**
   * 是否有小数点
   * @param o
   * @return
   */
  public static boolean isDoubleClass(Class<?> o) {
	  return  o == Double.class||o==Float.class;
  }
  
    /**
     * 是否是整数
     * @param o
     * @return
   */
    public static boolean isIntegerClass(Class<?> o) {
   
	    return o == Integer.class||o == Byte.class  || o == Short.class || o == Long.class
	    		   ||o==Integer.TYPE||o==Byte.TYPE||o==Short.TYPE||o==Long.TYPE;
    }
    /**
     * 是否为int integer
     * @param o
     * @return
     */
    public static boolean isIntClass(Class<?> o) {	   
	    return o == Integer.class||o==Integer.TYPE;
    }
    
    
    public static boolean isDateClss(Class<?> c) {
	    return c==Date.class||c==java.util.Date.class||c==Timestamp.class||c==Time.class;
    }
	public static boolean isArrayType(Class<?> type) {
		return type.isArray()||Iterable.class.isAssignableFrom(type);
	}


	/**
	 * 是否为数字
	 * @param type
	 * @return
	 */
	public static boolean isNumber(Class<?> type) {
		return type.isPrimitive()||Number.class.isAssignableFrom(type);
	}

}
