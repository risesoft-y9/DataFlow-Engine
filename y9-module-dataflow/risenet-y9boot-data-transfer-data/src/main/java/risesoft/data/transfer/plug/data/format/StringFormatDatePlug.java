package risesoft.data.transfer.plug.data.format;

import java.util.Base64;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.DateColumn;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.record.ColumnDisposeHandlePlug;
import risesoft.data.transfer.plug.data.utils.DateUtils;

/**
 * 将字符串进行格式化为时间
 * 
 * 
 * @typeName DateFormatPlug
 * @date 2024年8月7日
 * @author lb
 */
public class StringFormatDatePlug {
	/**
	 * 
	 * @param format     当不传的时候采用pattern 匹配常见格式
	 * @param field      字段
	 * @param jobContext
	 */
	public StringFormatDatePlug(@ConfigParameter(required = false, description = "格式") String format,
			@ConfigParameter(required = true, description = "列名") String field, JobContext jobContext) {
		if (StringUtils.isEmpty(format)) {
			ColumnDisposeHandlePlug.registerListener(field, (c, r, i) -> {
				Column column;
				try {
					String dateStr = c.asString();
					if (!StringUtils.isEmpty(dateStr)) {
						column = new DateColumn(DateUtils.parse(dateStr), c.getName());
						r.setColumn(i, column);
					} else {
						column = c;
					}

					return column;
				} catch (Exception e) {
					throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "时间格式化失败!:" + e.getMessage());
				}
			}, jobContext);
		}
		ColumnDisposeHandlePlug.registerListener(field, (c, r, i) -> {
			Column column;
			try {
				String dateStr = c.asString();
				if (!StringUtils.isEmpty(dateStr)) {
					column = new DateColumn(DateUtils.parse(dateStr, format), c.getName());
					r.setColumn(i, column);
				} else {
					column = c;
				}

				return column;
			} catch (Exception e) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "时间格式化失败!:" + e.getMessage());
			}
		}, jobContext);
	}

}
