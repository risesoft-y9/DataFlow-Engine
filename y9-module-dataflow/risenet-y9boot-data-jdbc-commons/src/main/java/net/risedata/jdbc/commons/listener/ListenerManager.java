package net.risedata.jdbc.commons.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ListenerManager {
  private static final Map<String, List<VariableRedirect>> REDIRECT_MAP = new ConcurrentHashMap<String, List<VariableRedirect>>();
  private static Object lock = new Object();
  public static void addRedirect(String key,VariableRedirect vr) {
	  synchronized (lock) {	 
		  List<VariableRedirect> redirects = REDIRECT_MAP.get(key);
		  if (redirects == null) {
			redirects = new ArrayList<VariableRedirect>();
			REDIRECT_MAP.put(key, redirects);
		  }
		  redirects.add(vr);
	  }
  }
  /**
   * 在class前面添加key
   * @param key
   * @return
   */
  public static String getClassKey(String key) {
	  StackTraceElement[] stackTraceElements= Thread.currentThread().getStackTrace();
	  return stackTraceElements[2].getClassName()+key;
  }
  public static <T> T redirect(String key,Object newValue,Class<T> T,Object... args) {
	  List<VariableRedirect> vrs = REDIRECT_MAP.get(key);
	  if (vrs!=null) {
		  for (VariableRedirect variableRedirect : vrs) {
			variableRedirect.Redirect(newValue,args);
		}
	  }
	  return T.cast(newValue);
  }
  public static  boolean  has(String key) {
	  synchronized (lock) {		
		  return REDIRECT_MAP.containsKey(key);
	  }
  }
  
}
