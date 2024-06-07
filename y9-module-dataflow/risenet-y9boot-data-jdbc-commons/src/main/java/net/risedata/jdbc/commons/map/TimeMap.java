package net.risedata.jdbc.commons.map;
/**
 * 带清除机制的timeMap
 * @author libo
 *2020年11月17日
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.risedata.jdbc.commons.utils.ThreadUtils;


public class TimeMap<K,V>  implements Map<K, V> {
    private Map<K, Node<V>> map;
    /**
     * 最大容量
     */
    private int maxKeySize = 1000;
	public TimeMap() {
		this.map = new HashMap<K, Node<V>>();
	}
	
	public TimeMap(int initialCapacity,int maxKeySize) {
		this.maxKeySize = maxKeySize;
		this.map = new HashMap<>(initialCapacity);
	}
	public TimeMap(int maxKeySize) {
		this.maxKeySize = maxKeySize;
		this.map = new HashMap<>();
	}
	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public V get(Object key) {
		Node<V> node = map.get(key);
		if (node!=null) {
			refreshTime(node);
			return node.getValue();
		}
		return null;
	}
    private void refreshTime(Node<V> node) {
        node.setTime(System.currentTimeMillis());
    }
	@Override
	public V put(K key, V value) {
		Node<V> node = new Node<V>(System.currentTimeMillis(), value);
		remove();
		return map.put(key, node).getValue();
	}
	/**
	 * 清除过期的key
	 */
	public void remove() {
		if (this.size()>=this.maxKeySize) {//遍历清除
			ThreadUtils.executor.execute(()->{				
				clearPast();
			});
		}
	}
	/**
	 * 清除执行方法防止多线程清除加锁
	 */
	private synchronized void clearPast() {
//        if (this.size()>=this.maxKeySize) {
//			Set<K> keys = map.keySet();
////			for (K k : keys) {	
////			}
//		}
	}
	
	public V put(K key, V value,long pastTime) {
		Node<V> node = new Node<V>(System.currentTimeMillis(), value,pastTime);
		remove();
		return map.put(key, node).getValue();
	}
	@Override
	public V remove(Object key) {
		Node<V> node = map.remove(key);
		return node==null?null:node.getValue();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {	
		Set<? extends K> keys =  m.keySet();
		for (K k : keys) {
			this.map.put(k,new Node<V>(System.currentTimeMillis(), m.get(k)));
		}
		remove();
	}

	@Override
	public void clear() {
      map.clear();		
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		Collection<Node<V>> nodes = map.values();
		Collection<V> values = new ArrayList<V>(nodes.size());
		for (Node<V> v : nodes) {
			values.add(v.getValue());
		}
		return values;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return null;
	}

}

class Node<V> implements Comparable<Node<V>>{
	private long time;
	private V value;
	private long pastTime = -1;
	 
	
	
	public Node(long time, V value, long pastTime) {
		super();
		this.time = time;
		this.value = value;
		this.pastTime = pastTime;
	}
	/**
	 * @return the pastTime
	 */
	public long getPastTime() {
		return pastTime;
	}
	/**
	 * @param pastTime the pastTime to set
	 */
	public void setPastTime(long pastTime) {
		this.pastTime = pastTime;
	}
	public Node(long time, V value) {
		super();
		this.time = time;
		this.value = value;
	}
	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
	/**
	 * @return the value
	 */
	public V getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(V value) {
		this.value = value;
	}
	@Override
	public int compareTo(@SuppressWarnings("rawtypes") Node o) {
		return (int) (o.time - this.time);
	}
	
}
