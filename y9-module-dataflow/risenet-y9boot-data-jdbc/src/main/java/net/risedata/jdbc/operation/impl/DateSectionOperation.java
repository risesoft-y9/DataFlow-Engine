package net.risedata.jdbc.operation.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;

/**
 * 操作时间区间的操作 在格外操作部分中 只需指定字段和时间即可
 * 
 * @author libo 2020年10月10日
 */
public class DateSectionOperation extends SimpleOperation {
	private Date maxDate;
	private Date minDate;

	/**
	 * @param maxDate 最大区间
	 */
	public DateSectionOperation(Date maxDate) {
		this.maxDate = maxDate;
	}

	/**
	 * 区间
	 * 
	 * @param maxDate  最大区间
	 * @param miniDate 最小区间
	 */
	public DateSectionOperation(Date maxDate, Date miniDate) {
		this.maxDate = maxDate;
		this.minDate = miniDate;
	}

	@Override
	public boolean where(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap,
			BeanConfig bc, Map<String, Object> excludeMap) {
		Object ovalue = minDate == null ? getValue(field, valueMap) : minDate;
		if (isNotNull(ovalue)) {
			sql.append(field.getColumn() + ">=" + "? ");
			args.add(ovalue);
			if (maxDate != null) {
				sql.append(" and " + field.getColumn() + "<=" + "? ");
				args.add(maxDate);
			}
			return true;
		} else if (maxDate != null) {
			if (maxDate != null) {
				sql.append(field.getColumn() + "<=" + "? ");
				args.add(maxDate);
			}
			return true;
		}
		return false;
	}

	@Override
	public int getOperate() {
		return 6;
	}

}
