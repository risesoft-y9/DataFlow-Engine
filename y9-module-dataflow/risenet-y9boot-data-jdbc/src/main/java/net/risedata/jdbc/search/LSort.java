package net.risedata.jdbc.search;
/**
 * 排序
 * @author libo
 *2020年10月10日
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LSort {
  private List<Order> orders = new ArrayList<Order>();
  private LSort() {
	
  }
  public static LSort toSort(String field,String order) {
	  LSort ls = new LSort();
	  ls.addOrder(field,order);
	  return ls;
  }
  /**
   * 创建一个排序对象
   * @param field 字段也可以是sql
   * @param order 排序 升序或者降序 
   * @param level 级别越小越靠前
   * @return
   */
  public static LSort toSort(String field,String order,int level) {
	  LSort ls = new LSort();
	  ls.addOrder(field,order,level);
	  return ls;
  }
  
  
  public static LSort toSort(List< Order> orders) {
	  LSort ls = new LSort();
	  ls.setOrders(orders);
	  return ls;
  }
  /**
   * 添加一个排序字段
   * @param field
   * @param order
   * @return
   */
  public LSort addOrder(String field,String order) {
    this.orders.add(new Order(field, order));
    return this;
  }
/**
 * 添加一个排序字段并添加级别
 * @param field
 * @param order
 * @param level
 * @return
 */
  public LSort addOrder(String field,String order,int level) {
    this.orders.add(new Order(field, order,level));
    return this;
  }
  /**
 * @param orders the orders to set
 */
public void setOrders(List<Order> orders) {
	this.orders = orders;
}
public LSort addOrder(Order o) {
	  this.orders.add(o);
	  return this;
  }
  public  LSort addAll(Collection<? extends Order> orders) {
	  this.orders.addAll(orders);
	  return this;
  }

	public List<Order> getOrders() {
		return orders;
	}
   
}
