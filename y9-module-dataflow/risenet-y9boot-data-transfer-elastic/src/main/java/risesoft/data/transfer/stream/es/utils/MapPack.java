package risesoft.data.transfer.stream.es.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapPack<K, V> implements Map<K, V> {
	public Map<K, V> map = new HashMap<>();
	MapPack(){
		
	}
	MapPack(K k,V v){
		map.put(k, v);
	}
	
	public MapPack<K, V> pu(K k,V v){
		map.put(k, v);
		return this;	
	}
	public V get(Object k) {
		return map.get(k);
	}
	
	public boolean has(K k) {
		return map.containsKey(k);
	}
	
	public Map<K, V> toMap(){
		return map;
	}
	public Map<K, V> toMap(K k,V v){
		map.put(k,v);
		return map;
	}
	@Override
	public int size() {
		return map.size();
	}
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
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
	public V remove(Object key) {
		return map.remove(key);
	}
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
      map.putAll(m);		
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
		return map.values();
	}
	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}
	@Override
	public V put(K key, V value) {
		return map.put(key, value);
	}
	@Override
	public String toString() {
		return   map.toString() ;
	}

}
