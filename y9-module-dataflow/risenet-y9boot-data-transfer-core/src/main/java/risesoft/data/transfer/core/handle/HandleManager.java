package risesoft.data.transfer.core.handle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.job.JobStartHandle;
import risesoft.data.transfer.core.record.ColumnDisposeHandle;
import risesoft.data.transfer.core.record.RecordInHandle;
import risesoft.data.transfer.core.util.ClassTools;

/**
 * 
 * 执行器管理器 管理所有的handle所有需要被使用的执行器都需要注册到其中
 * 
 * @typeName HandleManager
 * @date 2023年12月6日
 * @author lb
 */
@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
public class HandleManager implements JobStartHandle {

	private Map<Class<?>, HandleContext<? extends Handle>> handles = new HashMap<Class<?>, HandleContext<? extends Handle>>();
	private HandleContext<HandleChangeHandle> changeContext;

	public HandleManager() {
		add(this);
	}

	/**
	 * 如果这个 不存在则添加存在则跳过
	 * 
	 * @param handle
	 */
	public void addIfAbsent(Handle handle) {
		Set<Class<?>> classes = ClassTools.getInterfaceClass(handle.getClass());
		for (Class<?> class1 : classes) {
			if (Handle.class.isAssignableFrom(class1)) {
				HandleContext handleContext = getAndCreateContext(class1);
				if (!handleContext.contains(handle)) {
					handleContext.add(handle);
					changeContext.handle((h)->{
						h.doAdd(handle, class1);
					});
				}
			}
		}
	}

	/**
	 * 添加一个执行器
	 * 
	 * @param handle
	 */
	public void add(Handle handle) {
		Set<Class<?>> classes = ClassTools.getInterfaceClass(handle.getClass());
		for (Class<?> class1 : classes) {
			if (Handle.class.isAssignableFrom(class1)) {
				createContextAndAdd(class1, handle);
				changeContext.handle((h)->{
					h.doAdd(handle, class1);
				});
			}
		}
	}

	/**
	 * 创建上下文并添加对应执行器
	 * 
	 * @param class1
	 * @param handle
	 */
	private void createContextAndAdd(Class<?> class1, Handle handle) {
		HandleContext context = handles.get(class1);
		if (context == null) {
			context = new HandleContext();
			handles.put(class1, context);
		}
		context.add(handle);

	}

	/**
	 * 创建上下文并添加
	 * 
	 * @param class1
	 * @param handle
	 */
	private HandleContext<?> getAndCreateContext(Class<?> class1) {
		HandleContext context = handles.get(class1);
		if (context == null) {
			context = new HandleContext();
			handles.put(class1, context);
		}
		return context;

	}

	/**
	 * 根据执行器类型执行一次事件
	 * 
	 * @param <T>
	 * @param handleType
	 * @param doHandle
	 */
	public <T> void doHandle(Class<T> handleType, DoHandle<T> doHandle) {
		HandleContext handleContext = handles.get(handleType);
		if (handleContext != null) {
			handleContext.handle(doHandle);
		}

	}

	/**
	 * 获取执行器上下文
	 * 
	 * @param <T>
	 * @param handleType
	 * @return
	 */
	public <T> HandleContext<T> getContext(Class<T> handleType) {
		HandleContext handleContext = handles.get(handleType);
		if (handleContext != null) {
			return (HandleContext<T>) handles.get(handleType);
		}
		return EmptyHandleContext.EMPTY;
	}

	/**
	 * 判断一个执行器类型是否存在执行器
	 * 
	 * @param class1
	 * @return
	 */
	public boolean hasHandle(Class<?> class1) {
		return handles.containsKey(class1);
	}

	/**
	 * 移除一个插件
	 * 
	 * @param recordInHandle
	 */
	public void removePlug(Class<?> classType, Handle handle) {
		HandleContext handleContext = handles.get(classType);
		if (handleContext != null) {
			handles.remove(handle);
			changeContext.handle((h)->{
				h.doRemove(handle, classType);
			});
		}
	}

	/**
	 * 获取一个插件
	 * 
	 * @param <T>
	 * @param handleType 插件类型
	 * @param type       插件对象
	 * @return
	 */
	public <T> T getHandle(Class<?> handleType, Class<T> type) {
		HandleContext handleContext = handles.get(handleType);
		if (handleContext != null) {
			return (T) handleContext.getHandle(type);
		}
		return null;
	}

	@Override
	public void onJobStart(JobContext jobContext) {
		changeContext = getContext(HandleChangeHandle.class);
	}

}
