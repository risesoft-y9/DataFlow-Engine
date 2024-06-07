package net.risedata.jdbc.utils;
/**
 * 自定义的集合主要用于实现迭代器接口遍历一个数组
 * @author libo
 *2020年11月9日
 */

import java.util.Collection;
import java.util.Iterator;

public class LCollection<T> implements Collection<T>,Iterator<T> {
	private T[] element ;
	public LCollection(T[] element){
		this.element = element;
	}
	@Override
	public int size() {
		return element.length;
	}

	@Override
	public boolean isEmpty() {
		return size()==0;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}

	@Override
	public Object[] toArray() {
		return element;
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) element;
	}

	@Override
	public boolean add(T e) {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
	}
	private  int index = 0;
	@Override
	public boolean hasNext() {
		return index != element.length;
	}
	@Override
	public T next() {
        T t = element[index];
        index++;
 		return t;
	}
    
}
