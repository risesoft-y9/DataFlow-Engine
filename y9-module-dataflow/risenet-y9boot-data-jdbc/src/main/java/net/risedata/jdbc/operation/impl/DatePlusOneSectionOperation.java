package net.risedata.jdbc.operation.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.risedata.jdbc.commons.utils.DateUtils;
import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;

/**
 * 操作时间区间的操作 会根据当前字段的时候向后取一天用于区间
 * 
 * @author libo 2020年10月10日
 */
public class DatePlusOneSectionOperation extends SimpleOperation {

	@Override
	public boolean where(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap,
			BeanConfig bc, Map<String, Object> excludeMap) {

		Object ovalue = getValue(field, valueMap);
		if (isNotNull(ovalue)) {
			sql.append(field.getColumn() + ">=" + "? ");
			args.add(ovalue);
			sql.append(" and " + field.getColumn() + "<=" + "? ");
			args.add(DateUtils.updateDay((Date) ovalue, 1));
			return true;
		}
		return false;
	}

	@Override
	public int getOperate() {
		return 6;
	}

}
