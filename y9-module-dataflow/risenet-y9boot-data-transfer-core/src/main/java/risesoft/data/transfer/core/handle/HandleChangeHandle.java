package risesoft.data.transfer.core.handle;

/**
 * 监听handle改变
 * 
 * 
 * @typeName HandleChangeHandle
 * @date 2024年12月11日
 * @author lb
 */
public interface HandleChangeHandle extends Handle {
	/**
	 * 删除触发
	 * 
	 * @param handle     具体的对象
	 * @param handleType handle的类型
	 */
	void doRemove(Handle handle, Class<?> handleType);

	/**
	 * 添加触发
	 * 
	 * @param handle     具体的对象
	 * @param handleType handle的类型
	 */
	void doAdd(Handle handle, Class<?> handleType);

}
