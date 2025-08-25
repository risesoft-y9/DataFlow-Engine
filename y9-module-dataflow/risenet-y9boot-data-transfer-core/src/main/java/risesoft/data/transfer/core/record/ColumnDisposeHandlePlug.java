package risesoft.data.transfer.core.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.plug.Plug;

/**
 * 对于只要处理某些字段的插件进行统一遍历,这样不会出现多个插件多次反复循环遍历计算
 * 这个插件只能添加一次到插件管理器,需要注册这个插件，调用这个插件的静态方法
 * 
 * @typeName ColumnInHandlePlug
 * @date 2024年3月15日
 * @author lb
 */
public class ColumnDisposeHandlePlug extends AbstractRecordInHandle
		implements Plug, ColumnDisposeHandle, RecordInHandle {

	private static final String REGISTER_FIELD_IN_CONTEXT_KEY = "REGISTER_FIELD_IN_CONTEXT_KEY";

	private Map<String, List<ColumnDisposeHandle>> cache;

	private ColumnDisposeHandlePlug(JobContext jobContext) {
		cache = getCache(jobContext);
	}

	/**
	 * 在一个任务上下文中注册监听字段
	 * 
	 * @param field
	 * @param columnDisposeHandle
	 * @param jobContext
	 */
	public static synchronized void registerListener(String field, ColumnDisposeHandle columnDisposeHandle,
			JobContext jobContext) {
		ColumnDisposeHandlePlug cd = jobContext.getHandles().getHandle(ColumnDisposeHandle.class,
				ColumnDisposeHandlePlug.class);
		if (cd == null) {
			jobContext.getHandles().add(new ColumnDisposeHandlePlug(jobContext),jobContext);
		}
		Map<String, List<ColumnDisposeHandle>> cache = getCache(jobContext);
		List<ColumnDisposeHandle> handles = cache.get(field);
		if (handles == null) {
			handles = new ArrayList<ColumnDisposeHandle>();
			cache.put(field, handles);
		}
		handles.add(columnDisposeHandle);
	}

	/**
	 * 获取缓存
	 * 
	 * @param jobContext
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, List<ColumnDisposeHandle>> getCache(JobContext jobContext) {
		Object valueObject = jobContext.getContext().get(REGISTER_FIELD_IN_CONTEXT_KEY);
		if (valueObject == null) {
			valueObject = new HashMap<String, List<ColumnDisposeHandle>>();
			jobContext.getContext().put(REGISTER_FIELD_IN_CONTEXT_KEY, valueObject);
		}
		return (Map<String, List<ColumnDisposeHandle>>) valueObject;
	}

	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

	@Override
	public Column dispose(Column column, Record record, int index) {
		return column;
	}

	@Override
	public void doIn(Record record) {
		Column column;
		List<ColumnDisposeHandle> handles;
		for (int i = 0; i < record.getColumnNumber(); i++) {
			column = record.getColumn(i);
			handles = cache.get(column.getName());
			if (handles != null) {
				for (ColumnDisposeHandle columnDisposeHandle : handles) {
					column = columnDisposeHandle.dispose(column, record, i);
				}
			}
		}
	}

}
