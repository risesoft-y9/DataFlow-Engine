package net.risedata.jdbc.commons;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * 包装的循环对象
 * @author libo
 *2020年6月16日
 */
public class LForEach {
/**
 * 采用递增形式的for each 
 * @param index
 */
 public static<T>  void ForEachUp(int index,List<T> c,LFor<T> lf) {
	 for (int i = index; i < c.size(); i++) {
		lf.invoke(c.get(i), i);
	 }
 }
 /**
  * 采用递增形式的for each 
  * @param index
  */
  public static<T>  void ForEachUp(Collection<T> c,LFor<T> lf) {
 	    Integer index = 0;
 	    Iterator< T> i = c.iterator();
 		while(i.hasNext()) {
 			lf.invoke(i.next(), index++);
 		}
  }
  /**
   * 采用递增形式的for each 
   * @param index
   */
   public static<T>  void ForEachUp(T[] c,LFor<T> lf) {
  	    for (int i = 0; i < c.length; i++) {
			lf.invoke(c[i], i);
		}
   }
  
  
/**
 * 采用递增减形式的for each 
 * @param index
 */
public static<T>  void ForEachDown(int index,List<T> c,LFor<T> lf) {
	 for (int i = index; i >= 0; i--) {
		lf.invoke(c.get(i), i);
	 }
}

/**
 * 采用递增减形式的for each 
 * @param index
 */
public static<T>  void ForEachDown(List<T> c,LFor<T> lf) {
	 for (int i = c.size(); i >= 0; i--) {
		lf.invoke(c.get(i), i);
	 }
}
}
