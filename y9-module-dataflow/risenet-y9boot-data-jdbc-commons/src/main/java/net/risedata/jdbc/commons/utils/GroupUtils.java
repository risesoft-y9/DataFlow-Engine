package net.risedata.jdbc.commons.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupUtils {
	/**
	 * 对list分组
	 * 
	 * @param <K>
	 * @param <V>
	 * @param persons 值
	 * @param getKey  key
	 * @return
	 */
	public static <K, V> Map<K, List<V>> group(Collection<V> persons, GetKey<K, V> getKey) {

		Map<K, List<V>> result = new HashMap<K, List<V>>();
		K tmpK;
		List<V> tmpV;
		for (V v : persons) {
			tmpK = getKey.getKey(v);
			tmpV = result.get(tmpK);
			if (tmpV == null) {
				tmpV = new ArrayList<>();
				result.put(tmpK, tmpV);
			}
			tmpV.add(v);
		}
		return result;

	}

	/**
	 * 对list分组
	 * 
	 * @param <K>
	 * @param <V>
	 * @param persons 值
	 * @param getKey  key
	 * @return
	 */
	public static <K, V> Map<K, V> groupMap(Collection<V> persons, GetKey<K, V> getKey) {

		Map<K, V> result = new HashMap<K, V>();
		K tmpK;
		for (V v : persons) {
			tmpK = getKey.getKey(v);
			result.put(tmpK, v);
		}
		return result;

	}

}
