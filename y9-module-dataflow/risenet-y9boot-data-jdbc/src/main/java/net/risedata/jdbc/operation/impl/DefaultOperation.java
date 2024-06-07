package net.risedata.jdbc.operation.impl;

import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;

/**
 * 其他基础的操作 例如 >= <= = < > like =
 * 
 * @author libo 2020年10月9日
 */
public class DefaultOperation extends SimpleOperation {
	String doperation = "";

	public DefaultOperation(String doperation) {
		this.doperation = doperation.toLowerCase();
	}

	@Override
	public boolean where(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap,
			BeanConfig bc, Map<String, Object> excludeMap) {
		Object ovalue = getValue(field, valueMap);
		if (isNotNull(ovalue)) {// 不为空则拼接
			args.add(ovalue);
			sql.append(n + field.getColumn() + doperation + "?");
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "DefaultOperation [doperation=" + doperation + "]";
	}

	@Override
	public int getOperate() {
		switch (doperation) {// 优先级排序 本身不带sql优化所以返回一个固定值
		case "=":
			return 1;
		case ">":
			return 2;
		case "<":
			return 2;
		case ">=":
			return 3;
		case "<=":
			return 3;
		case "NOT IN":
			return 4;
		default:
			return 6;
		}

	}

}
