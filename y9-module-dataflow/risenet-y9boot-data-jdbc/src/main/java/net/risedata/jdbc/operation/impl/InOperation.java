package net.risedata.jdbc.operation.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.risedata.jdbc.commons.TypeCheck;
import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.factory.HandleMappingFactory;

/**
 * 操作符 in 的实现
 * 
 * @author libo 2020年10月9日
 */
public class InOperation extends SimpleOperation {
	private String in = "";// 占位符
	private String eq = "";
	private boolean isIn = false;
	private Object val;

	public InOperation(boolean b) {
		if (b) {
			in = " IN ";
			eq = " = ?";

		} else {
			eq = " != ?";
			in = " NOT IN ";
		}
		this.isIn = b;

	}

	/**
	 * 
	 * @param b   in and not in
	 * @param val
	 */
	public InOperation(boolean b, Object val) {
		this(b);
		this.val = val;
	}

	/**
	 * 不为空则进行拼接 使用占位符? 的形式凭借最后传递参数交由jdbctemplate 实现
	 */
	@Override
	public boolean where(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap,
			BeanConfig bc, Map<String, Object> excludeMap) {
		Object ovalue = val == null ? getValue(field, valueMap) : val;
		if (isNotNull(ovalue)) {// 不为空则拼接
			if (ovalue instanceof String) {// 对于string 的in 操作
				String value = (String) ovalue;
				if (value.indexOf(",") != -1) {// 当 string 中包含,号才分割 否则 in 会变成 = or !=
					String[] values = value.split(",");
					addArgs(values, args, sql, field);
					return true;
				} else {// 没有
					args.add(ovalue);
					if (isIn) {// not and !
						sql.append(field.getColumn() + eq);
					} else {
						sql.append(field.getColumn() + eq);
					}
					return true;
				}
			} else {// array
				if (ovalue.getClass().isArray()) {// array
					Object[] oarr = (Object[]) ovalue;
					addArgs(oarr, args, sql, field);
					return true;
				} else if (Iterable.class.isAssignableFrom(ovalue.getClass())) {
					@SuppressWarnings("unchecked")
					Iterator<Object> iterator = ((Iterable<Object>) ovalue).iterator();
					sql.append(field.getColumn() + in + "(");

					while (iterator.hasNext()) {
						args.add(HandleMappingFactory.parse(iterator.next(), field.getFieldType()));
						if (iterator.hasNext()) {// 没有下一个的时候则是最后一个
							sql.append("?,");
						} else {
							sql.append("?)");
						}
					}
					;
					return true;
				} else {// other
					if (TypeCheck.isNumber(ovalue.getClass())) {// number
						args.add(ovalue);
						sql.append(field.getColumn() + eq);
						return true;
					}
					args.add(ovalue);
					sql.append(field.getColumn() + in + "(?)");
					return true;
				}
			}
		}
		return false;
	}

	private void addArgs(Object[] arr, List<Object> args, StringBuilder sql, FieldConfig field) {
		if (arr.length == 1) {
			args.add(HandleMappingFactory.parse(arr[0], field.getFieldType()));
			if (isIn) {// not and !
				sql.append(field.getColumn() + eq);
			} else {
				sql.append(field.getColumn() + eq);
			}
		} else {
			sql.append(field.getColumn() + in + "(");
			for (int i = 0; i < arr.length; i++) {// 拼接
				args.add(HandleMappingFactory.parse(arr[i], field.getFieldType()));
				if (i == arr.length - 1) {
					sql.append("?)");
				} else {
					sql.append("?,");
				}
			}
		}
	}

	@Override
	public String toString() {
		return "InOperation [in=" + in + "]";
	}

	@Override
	public int getOperate() {
		return 4;
	}

}
