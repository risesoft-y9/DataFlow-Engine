package risesoft.data.transfer.plug.data.format;

import java.util.Base64;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
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
public class DateFormatStringPlug {
	
	public DateFormatStringPlug(@ConfigParameter(required = true, description = "格式") String format,
			@ConfigParameter(required = true, description = "列名") String field, JobContext jobContext) {
		ColumnDisposeHandlePlug.registerListener(field, (c, r, i) -> {
			Column column;
			try {
				Date date = c.asDate();
				if (date!=null) {
					column = new StringColumn(
							DateUtils.format(date, format) ,
							c.getName());
					r.setColumn(i, column);
				}else {
					column = c;
				}
				
				return column;
			} catch (Exception e) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "时间格式化失败!:" + e.getMessage());
			}
		}, jobContext);
	}

}
