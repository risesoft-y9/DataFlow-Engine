package risesoft.data.transfer.plug.data.transfer;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.plug.Plug;
import risesoft.data.transfer.core.record.ColumnDisposeHandlePlug;

/**
 * 数据转换插件
 * 
 * @typeName DataTransferPlug
 * @date 2024年8月8日
 * @author lb
 */
public class DataTransferPlug implements Plug {

	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

	public DataTransferPlug(@ConfigParameter(required = true, description = "列名") String field, JobContext jobContext,
			@ConfigParameter(required = true, description = "转换映射") Map<String, String> transferMapping) {
		ColumnDisposeHandlePlug.registerListener(field, (c, r, i) -> {
			Column column;
			try {
				String mapping = transferMapping.get(c.asString());
				if (!StringUtils.isEmpty(mapping)) {
					column = new StringColumn(mapping, c.getName());
					r.setColumn(i, column);
					return column;
				}
				return c;
			} catch (Exception e) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "数据转换失败!:" + e.getMessage());
			}
		}, jobContext);
	}

}
