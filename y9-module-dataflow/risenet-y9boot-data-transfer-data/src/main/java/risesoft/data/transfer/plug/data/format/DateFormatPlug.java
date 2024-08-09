package risesoft.data.transfer.plug.data.format;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.DateColumn;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.plug.Plug;
import risesoft.data.transfer.core.record.ColumnDisposeHandlePlug;
import risesoft.data.transfer.plug.data.utils.DateUtils;

/**
 * 将时间进行格式化修改
 * 
 * 
 * @typeName DateFormatPlug
 * @date 2024年8月7日
 * @author lb
 */
public class DateFormatPlug implements Plug {

	public DateFormatPlug(@ConfigParameter(required = false, description = "格式") String format,
			@ConfigParameter(required = true, description = "列名") String field, JobContext jobContext) {
		boolean bool = StringUtils.isNotBlank(format);
		ColumnDisposeHandlePlug.registerListener(field, (c, r, i) -> {
			Column column;
			try {
				if (c.getType() == Column.Type.DATE) {
					Date date = c.asDate();
					if (date != null) {
						if(!bool) {
							throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "时间格式化失败，date字段类型时format格式信息不能为空");
						}
						column = new StringColumn(DateUtils.format(date, format), c.getName());
						r.setColumn(i, column);
						return column;
					}
				} else {
					String dateStr = c.asString();
					if (!StringUtils.isEmpty(dateStr)) {
						if(bool) {
							column = new DateColumn(DateUtils.parse(dateStr, format), c.getName());
						}else {
							column = new DateColumn(DateUtils.parse(dateStr), c.getName());
						}
						r.setColumn(i, column);
						return column;
					}
				}
				return c;
			} catch (Exception e) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "时间格式化失败!:" + e.getMessage(), e);
			}
		}, jobContext);
	}

	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

}
