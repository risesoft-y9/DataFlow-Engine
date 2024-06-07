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
public class SectionOperation extends SimpleOperation {
	private Object max;
	private Object min;

	/**
	 * @param maxDate 最大区间
	 */
	public SectionOperation(Object max) {
		this.max = max;
	}

	/**
	 * 区间
	 * 
	 * @param maxDate  最大区间
	 * @param miniDate 最小区间
	 */
	public SectionOperation(Object mini, Object max) {
		this.max = max;
		this.min = mini;
	}

	@Override
	public boolean where(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap,
			BeanConfig bc, Map<String, Object> excludeMap) {
		Object ovalue = min == null ? getValue(field, valueMap) : min;
		if (isNotNull(ovalue)) {
			sql.append(field.getColumn() + ">=" + "? ");
			args.add(ovalue);
			if (max != null) {
				sql.append(" and " + field.getColumn() + "<=" + "? ");
				args.add(max);
			}
			return true;
		} else if (max != null) {
			if (max != null) {
				sql.append(field.getColumn() + "<=" + "? ");
				args.add(max);
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
