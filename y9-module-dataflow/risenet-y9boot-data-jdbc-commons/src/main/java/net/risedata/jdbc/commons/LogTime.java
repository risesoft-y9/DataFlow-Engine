package net.risedata.jdbc.commons;

import java.util.Date;

public class LogTime {
  private static ThreadLocal<Long> start = new ThreadLocal<>();
  public static void start() {
	  System.out.println("start:"+new Date());
	  start.set(System.currentTimeMillis());
  }
  
  public static void print() {
	  System.out.println("time:"+(System.currentTimeMillis()-start.get())+"ms");
  }
  
  public static Long get() {
	  return System.currentTimeMillis()-start.get();
  }

}
