package risesoft.data.transfer.core.util.strings.handles;

/**
 * 值改变成其他值的handle
 * 
 * @typeName CastValueHandle
 * @date 2024年1月29日
 * @author lb
 * @param <T>
 * @param <V>
 */
public interface CastValueHandle<T, V> {

	Class<?>[] getTypes();

	T getValue(V value);
}
