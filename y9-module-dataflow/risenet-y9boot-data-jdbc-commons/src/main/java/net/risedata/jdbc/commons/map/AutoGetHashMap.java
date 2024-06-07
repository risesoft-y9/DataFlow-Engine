package net.risedata.jdbc.commons.map;

import java.util.HashMap;
import java.util.List;

public class AutoGetHashMap<K, V> extends HashMap<K, V> {

	private Init<V> init;

	public AutoGetHashMap(Init<V> init) {
		this.init = init;
	}

	

	@Override
	public V get(Object key) {
		V v = super.get(key);
		if (v == null) {
			v = init.create();
			super.put((K) key, v);
		}
		return v;
	}



	public List<String> get(String key, int length) {
		List<String> list = (List<String>) get(key);
		if (list.size()<length) {
			for (int i = 0; i < length-list.size(); i++) {
				list.add("");
			}
		}
		return list;
	}

}

