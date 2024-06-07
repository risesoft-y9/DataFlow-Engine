package net.risedata.jdbc.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.risedata.jdbc.operation.Operation;

/**
 * 用于构建operation
 * 
 * @author libo 2021年7月28日
 */
public class OperationBuilder implements Map<String, Operation> {
	private Map<String, Operation> operationMap;

	public OperationBuilder() {
		operationMap = new HashMap<String, Operation>();
	}

	public OperationBuilder builder(String key, Operation value) {
		put(key, value);
		return this;
	}

	@Override
	public int size() {

		return operationMap.size();
	}

	@Override
	public boolean isEmpty() {
		return operationMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return operationMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return operationMap.containsValue(value);
	}

	@Override
	public Operation get(Object key) {
		return operationMap.get(key);
	}

	@Override
	public Operation put(String key, Operation value) {
		return operationMap.put(key, value);
	}

	@Override
	public Operation remove(Object key) {
		return operationMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Operation> m) {
		operationMap.putAll(m);
	}

	@Override
	public void clear() {
		operationMap.clear();
	}

	@Override
	public Set<String> keySet() {
		return operationMap.keySet();
	}

	@Override
	public Collection<Operation> values() {
		return operationMap.values();
	}

	@Override
	public Set<Entry<String, Operation>> entrySet() {
		return operationMap.entrySet();
	}

}
