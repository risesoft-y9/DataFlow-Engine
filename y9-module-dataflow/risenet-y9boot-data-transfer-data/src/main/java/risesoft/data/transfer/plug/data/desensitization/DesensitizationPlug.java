package risesoft.data.transfer.plug.data.desensitization;

import java.util.List;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.plug.Plug;
import risesoft.data.transfer.core.record.ColumnDisposeHandlePlug;

/**
 * 字段脱敏 会把原有的字段变成string 类型 并在数据中间增加**
 * 
 * @typeName DesensitizationPlug
 * @date 2024年3月12日
 * @author lb
 */
public class DesensitizationPlug implements Plug {

	public DesensitizationPlug(
			@ConfigParameter(required = true, description = "脱敏字段") List<String> desensitizationFields,
			JobContext jobContext) {

		for (String field : desensitizationFields) {
			ColumnDisposeHandlePlug.registerListener(field, (column, record, index) -> {
				try {
					Column newColumn = new StringColumn(DesensitizationUtils.desensitization(column.asString()),
							column.getName());
					record.setColumn(index, newColumn);
					return newColumn;
				} catch (Exception e) {
					throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "在数据脱敏时出现异常:" + e.getMessage());
				}
			}, jobContext);
		}
	}

	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

}
