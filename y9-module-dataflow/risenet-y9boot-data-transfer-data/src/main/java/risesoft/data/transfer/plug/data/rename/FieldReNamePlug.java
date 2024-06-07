package risesoft.data.transfer.plug.data.rename;

import java.util.List;
import java.util.Map;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.factory.annotations.ConfigBean;
import risesoft.data.transfer.core.factory.annotations.ConfigField;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.plug.Plug;
import risesoft.data.transfer.core.record.AbstractRecordInHandle;
import risesoft.data.transfer.core.record.ColumnDisposeHandlePlug;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.record.RecordInHandle;

/**
 * 字段名字重命名的插件用于修改输入数据的字段的字段名字
 * 
 * @typeName FieldReNamePlug
 * @date 2024年3月8日
 * @author lb
 */
public class FieldReNamePlug implements Plug {

	public FieldReNamePlug(@ConfigParameter(description = "重命名字段映射", required = true) Map<String, String> renameMap,
			JobContext jobContext) {
		renameMap.forEach((k, v) -> {
			ColumnDisposeHandlePlug.registerListener(k, (c, r, i) -> {
				c.setName(v);
				return c;
			}, jobContext);

		});
	}


	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

}
