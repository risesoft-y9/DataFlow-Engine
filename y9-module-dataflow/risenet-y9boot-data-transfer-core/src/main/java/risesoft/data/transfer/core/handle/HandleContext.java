package risesoft.data.transfer.core.handle;

import java.util.ArrayList;
import java.util.List;

/**
 * 上下文 用于获取执行器上下文进行快速循环遍历,减少判断带来的性能消耗
 * 每一个执行器只存储一种handle
 * 
 * @typeName HandleContext
 * @date 2023年12月6日
 * @author lb
 * @param <F>
 */
public class HandleContext<H> {

	private List<H> handles;

	public HandleContext() {
		handles = new ArrayList<H>();

	}

	/**
	 * 是否存在
	 * 
	 * @param handle
	 * @return
	 */
	public boolean contains(H handle) {
		return handles.contains(handle);

	}

	/**
	 * 移除一个元素
	 * 
	 * @param handle
	 * @return
	 */
	public boolean remove(H handle) {
		return handles.remove(handle);
	}

	/**
	 * 添加一个执行器
	 * 
	 * @param handle
	 */
	public void add(H handle) {
		handles.add(handle);
	}

	/**
	 * 执行对应执行器
	 * 
	 * @param doHandle
	 */
	public void handle(DoHandle<H> doHandle) {
		for (int i = 0; i < handles.size(); i++) {
			doHandle.doHandle(handles.get(i));
		}
	}

	public <T> T getHandle(Class<T> type) {
		for (int i = 0; i < handles.size(); i++) {
			if (type.isAssignableFrom(handles.get(i).getClass())) {
				return (T) handles.get(i);
			}
		}
		return null;
	}

}
