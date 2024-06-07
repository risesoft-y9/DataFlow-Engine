package net.risedata.jdbc.config.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.risedata.jdbc.commons.TypeCheck;
import net.risedata.jdbc.mapping.HandleMapping;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.operation.OperationPack;

/**
 * 保存每一个字段的config
 * 
 * @author libo 2020年10月9日
 */
public class FieldConfig implements Comparable<FieldConfig> {
	/**
	 * 是否为 update时候所需的id
	 */
	private boolean isUpdateId = false;
	/**
	 * 是否同时开启update 以及where
	 */
	private boolean updateWhere = false;
	/**
	 * update,insert 的时候去检测此字段的数据库是否存在 update时会 != id 同时 and field = value 如果返回为 1
	 * 则不会更新会抛异常
	 */
	private boolean updateCheck = false;

	/**
	 * 是否为占位当没有传入自定义操作的时候不生效
	 */
	private boolean isPlaceholder = false;
	/**
	 * 是否为entiry
	 */
	private boolean isEntiry = false;
	/**
	 * 字段名
	 */
	private String fieldName;
	/**
	 * 是否为 Transient 也就是在映射时候不使用的 查询的时候会* 只是在 映射的时候没有从reset中get
	 */
	private boolean isTransient;
	/**
	 * 参数映射的handle
	 */
	@SuppressWarnings("rawtypes")
	private HandleMapping handle;
	/**
	 * 对应的字段对象
	 */
	private Field field;
	/**
	 * 在get 数据的时候的field 正常情况下此valueField = field 为 sreachable使用
	 */
	private Field valueField;

	/**
	 * 在set值的时候使用的field 为 sreachable使用
	 */
	private Field setValueField;
	/**
	 * 字段类型 与set方法一致
	 */
	private Class<?> fieldType;
	/**
	 * 是否为id
	 */
	private boolean isId;
	/**
	 * 对应的数据库字段名
	 */
	private String column;

	/**
	 * 运算符操作
	 */
	private List<OperationPack> OperationPacks;

	/**
	 * 默认的操作
	 */
	private Operation defaultOperation;

	/**
	 * @return the field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * @return the updateWhere
	 */
	public boolean isUpdateWhere() {
		return updateWhere;
	}

	/**
	 * @param updateWhere the updateWhere to set
	 */
	public void setUpdateWhere(boolean updateWhere) {
		this.updateWhere = updateWhere;
	}

	/**
	 * @return the isUpdateId
	 */
	public boolean isUpdateId() {
		return isUpdateId;
	}

	/**
	 * @param isUpdateId the isUpdateId to set
	 */
	public void setUpdateId(boolean isUpdateId) {
		this.isUpdateId = isUpdateId;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(Field field) {
		
		this.field = field;
	}

	/**
	 * @return the isEntiry
	 */
	public boolean isEntiry() {
		return isEntiry;
	}

	/**
	 * @param isEntiry the isEntiry to set
	 */
	public void setEntiry(boolean isEntiry) {
		this.isEntiry = isEntiry;
	}

	/**
	 * @return the column
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * @return the valueField
	 */
	public Field getValueField() {
		return valueField;
	}

	/**
	 * @param valueField the valueField to set
	 */
	public void setValueField(Field valueField) {
		valueField.setAccessible(true);
		this.valueField = valueField;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * @return the fieldType
	 */
	public Class<?> getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType the fieldType to set
	 */
	public void setFieldType(Class<?> fieldType) {

		this.fieldType = fieldType;
	}

	/**
	 * @return the isId
	 */
	public boolean isId() {
		return isId;
	}

	/**
	 * @param isId the isId to set
	 */
	public void setId(boolean isId) {
		this.isId = isId;
	}

	/**
	 * @return the operation
	 */
	public Operation getOperation(Map<String, Object> valueMap) {
		if (this.OperationPacks != null) {
			for (OperationPack operationPack : OperationPacks) {

				if (operationPack.condition(valueMap)) {
					return operationPack.getOperation();
				}
			}
		}
		return defaultOperation;
	}

	/**
	 * @return the defaultOperation
	 */
	public Operation getDefaultOperation() {
		return defaultOperation;
	}

	/**
	 * @param defaultOperation the defaultOperation to set
	 */
	public void setDefaultOperation(Operation defaultOperation) {

		this.defaultOperation = defaultOperation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(Operation operation) {
		this.defaultOperation = operation;
	}

	public void addOperationPack(OperationPack op) {
		if (this.OperationPacks == null) {
			this.OperationPacks = new ArrayList<>();
		}
		this.OperationPacks.add(op);
	}

	/**
	 * @return the isTransient
	 */
	public boolean isTransient() {
		return isTransient;
	}

	/**
	 * @param isTransient the isTransient to set
	 */
	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	@Override
	public String toString() {
		return "FieldConfig [ fieldName=" + fieldName + ", handle=" + handle + ", field=" + field + ", fieldType="
				+ fieldType + ", column=" + column + ", operation=" + OperationPacks + "]";
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the setValueField
	 */
	public Field getSetValueField() {
		return setValueField;
	}

	/**
	 * @param setValueField the setValueField to set
	 */
	public void setSetValueField(Field setValueField) {
		this.setValueField = setValueField;
	}

	@Override
	public int compareTo(FieldConfig o) {
		return compare(o);
	}

	private int compare(FieldConfig o) {
		int operate = o.defaultOperation.getOperate();
		int myOperate = this.defaultOperation.getOperate();
		int index = myOperate - operate;// 比对的对象减去自己
		if (index != 0) {
			return index;
		}
		if (o.isId) {// id优先
			if (!this.isId) {// 对方是我不是
				return -1;
			}
		}
		if (TypeCheck.isIntegerClass(o.getFieldType()) && !TypeCheck.isIntegerClass(this.getFieldType())) {// 整数优先
			return -1;
		}
		return 0;
	}

	/**
	 * @return the handle
	 */
	public HandleMapping<?> getHandle() {
		return handle;
	}

	/**
	 * @param handle the handle to set
	 */
	public void setHandle(HandleMapping<?> handle) {
		this.handle = handle;
	}

	/**
	 * @return the isPlaceholder
	 */
	public boolean isPlaceholder() {
		return isPlaceholder;
	}

	/**
	 * @param isPlaceholder the isPlaceholder to set
	 */
	public void setPlaceholder(boolean isPlaceholder) {
		this.isPlaceholder = isPlaceholder;
	}

	/**
	 * @return the updateCheck
	 */
	public boolean isUpdateCheck() {
		return updateCheck;
	}

	/**
	 * @param updateCheck the updateCheck to set
	 */
	public void setUpdateCheck(boolean updateCheck) {
		this.updateCheck = updateCheck;
	}

	public void init(Class<?> bean) {
		if (OperationPacks != null) {
			for (OperationPack operationPack : OperationPacks) {
				operationPack.init(bean);
			}
		}
	}

}
