package net.risedata.jdbc.config.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import net.risedata.jdbc.exception.InstanceException;
import net.risedata.jdbc.factory.ConditionProxyFactory;
import net.risedata.jdbc.mapping.ColumnMapping;
import net.risedata.jdbc.search.Order;

public class BeanConfig {
	public static final int SELECT_INSERT_INDEX = 7;

	private int fromLenth = -1;
	private String allSelects;
	private Class<?> cla;
	private boolean sync;
	private String tableFrom;
	/**
	 * 特殊的set list
	 */
	List<FieldConfig> rowMappingList;

	/**
	 * 表名字
	 */
	private String tableName;
	/**
	 * 删除的sql
	 */
	private String delSql;
	/**
	 * 构造器
	 */
	private Constructor<?> constructor;
	/**
	 * 排序
	 */
	private List<Order> orders = new ArrayList<>();
	/**
	 * map形式 的config
	 */
	private Map<String, FieldConfig> fields;
	/**
	 * 每个字段的排序位置的map
	 */
	private Map<String, Integer> indexMap;
	/**
	 * 所有字段的fieldconfig
	 */
	private List<FieldConfig> fieldlist = new ArrayList<FieldConfig>();
	/**
	 * id config 包括 updte时候的id
	 */
	private List<FieldConfig> idField = new ArrayList<>();
	/**
	 * 连接其他表配置
	 */
	private List<JoinConfig> joins;
	/**
	 * 表的选择区域
	 */
	private String tableAsAll;
	/**
	 * 表别名
	 */
	private String tableAs;

	/**
	 * 查询时候的sql语句
	 */
	private String selectTableSql;
	/**
	 * 对于update insert 在操作的时候需要先检查数据库是否存在的操作
	 */
	private List<FieldConfig> checkedField;
	/**
	 * 非 id field
	 */
	private List<FieldConfig> noIdFiled = new ArrayList<>();
	/**
	 * 拿到所有的配置
	 */
	private List<FieldConfig> allFields = new ArrayList<>();
	/**
	 * count sql
	 */
	private String countTableSql;

	private volatile List<ColumnMapping> columnMappings;

	public List<ColumnMapping> columnMappings() {
		return columnMappings;
	}

	public void addColumnMapping(ColumnMapping columnMapping) {
		if (columnMapping == null) {
			return;
		}
		if (columnMappings == null) {
			synchronized (this) {
				if (columnMappings == null) {
					columnMappings = new ArrayList<ColumnMapping>();
				}
			}
		}
		columnMappings.add(columnMapping);
	}

	public void addJoin(JoinConfig join) {
		if (joins == null) {
			joins = new ArrayList<JoinConfig>();
		}
		joins.add(join);
	}

	public Object getValue(Object value, String filedName) {
		FieldConfig fieldcConfig = this.getField(filedName);
		if (fieldcConfig == null) {
			return null;
		}
		try {
			return fieldcConfig.getField().get(value);
		} catch (Exception e) {
			throw new InstanceException("get value error " + e.getMessage());
		}

	}

	/**
	 * 拿到一个join的配置
	 * 
	 * @param tableClass
	 * @return
	 */
	public JoinConfig getJoin(Class<?> tableClass) {
		if (joins == null) {
			joins = new ArrayList<>();
		}
		for (JoinConfig join : joins) {
			if (join.getTableClass() == tableClass) {
				return join;
			}
		}
		return null;
	}

	public void putField(String fieldName, FieldConfig fc) {
		if (!fc.isTransient()) {// 非 transient
			this.fieldlist.add(fc);
		} // 还是会存在于map中
		allFields.add(fc);// 全部的字段配置
		if (fields == null) {
			fields = new HashMap<>();
		}
		fields.put(fieldName, fc);
	}

	public FieldConfig getField(String fieldName) {
		return fields.get(fieldName);
	}

	public Integer getIndex(String field) {
		return indexMap.get(field);
	}

	/**
	 * @return the indexMap
	 */
	public Map<String, Integer> getIndexMap() {
		return indexMap;
	}

	/**
	 * @param delSql the delSql to set
	 */
	public void setDelSql(String delSql) {
		this.delSql = delSql;
	}

	/**
	 * @return the sync
	 */
	public boolean isSync() {
		return sync;
	}

	/**
	 * @param sync the sync to set
	 */
	public void setSync(boolean sync) {
		this.sync = sync;
	}

	/**
	 * @param selectTableSql the selectTableSql to set
	 */
	public void setSelectTableSql(String selectTableSql) {
		this.selectTableSql = selectTableSql;
	}

	/**
	 * @param countTableSql the countTableSql to set
	 */
	public void setCountTableSql(String countTableSql) {
		this.countTableSql = countTableSql;
	}

	public void sort() {
		Collections.sort(this.orders);
		for (Order o : this.orders) {
			if (StringUtils.isNotBlank(o.getExpression()) && o.getCondition() == null) {
				// 在加载字段配置的时候bean还没有生成造成死循环
				o.setCondition(ConditionProxyFactory.getInstance(o.getExpression(), this.cla));
			}
		}
		Collections.sort(this.fieldlist);
		indexMap = new HashMap<String, Integer>();
		for (int i = 0; i < fieldlist.size(); i++) {
			indexMap.put(fieldlist.get(i).getFieldName(), i);
		}
		idField.clear();
		for (FieldConfig fieldConfig : fieldlist) {
			if (fieldConfig.isUpdateCheck()) {// 修改时候的check
				if (this.checkedField == null) {
					this.checkedField = new ArrayList<>();
				}
				this.checkedField.add(fieldConfig);
			}
			if (fieldConfig.isId() || fieldConfig.isUpdateId()) { // update 时候的id
				idField.add(fieldConfig);
				if (fieldConfig.isUpdateWhere()) {
					noIdFiled.add(fieldConfig);
				}
			} else {
				noIdFiled.add(fieldConfig);
			}
			fieldConfig.init(cla);
		}

		this.tableAs = getBeanAsName(cla);
		int index = 0;
		if (joins != null) {
			for (JoinConfig jc : joins) {
				index++;
				jc.toSql(tableAs + index, tableAs);
			}
		}
		this.tableAsAll = this.tableAs + ".*";
		StringBuilder selects = new StringBuilder();
		for (int i = 0; i < fieldlist.size(); i++) {
			selects.append(tableAs + "." + fieldlist.get(i).getColumn());
			if (i != fieldlist.size() - 1) {
				selects.append(",");
			}
		}

		fromLenth = selects.length() + SELECT_INSERT_INDEX;
		if (selectTableSql != null) {
			return;
		}
		this.selectTableSql = "SELECT " + selects + " FROM " + this.tableName + " " + tableAs;
		if (allFields.size() > fieldlist.size()) {
			StringBuilder selectsAll = new StringBuilder();
			for (int i = 0; i < allFields.size(); i++) {
				selectsAll.append(tableAs + "." + allFields.get(i).getColumn());
				if (i != allFields.size() - 1) {
					selectsAll.append(",");
				}
			}
			allSelects = "SELECT " + selectsAll.toString() + " FROM " + this.tableName + " " + tableAs;
		} else {
			allSelects = selects.toString();
		}

		this.countTableSql = "SELECT COUNT(1) COUNT FROM " + this.tableName + " " + tableAs;
		this.tableFrom = "FROM " + this.tableName + " " + tableAs;
		this.delSql = "DELETE FROM " + this.tableName;
		if (this.checkedField != null) {
			this.checkSql = " SELECT COUNT(1) FROM " + tableName + " ";

		}
	}

	private String checkSql;

	/**
	 * @return the checkSql
	 */
	public String getCheckSql() {
		return checkSql;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(Map<String, FieldConfig> fields) {
		this.fields = fields;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		return "BeanConfig [cla=" + cla + ", tableName=" + tableName + ", fields=" + fields + "]";
	}

	/**
	 * @return the idFiled
	 */
	public List<FieldConfig> getIdField() {
		return idField;
	}

	/**
	 * @return the noIdFiled
	 */
	public List<FieldConfig> getNoIdField() {
		return noIdFiled;
	}

	/**
	 * @return the tableFrom
	 */
	public String getTableFrom() {
		return tableFrom;
	}

	/**
	 * @param tableFrom the tableFrom to set
	 */
	public void setTableFrom(String tableFrom) {
		this.tableFrom = tableFrom;
	}

	/**
	 * @return the orders
	 */
	public List<Order> getOrders() {
		return orders;
	}

	public void addOrder(Order order) {
		this.orders.add(order);
	}

	/**
	 * @return the checkedField
	 */
	public List<FieldConfig> getCheckedField() {
		return checkedField;
	}

	/**
	 * @return the cla
	 */
	public Class<?> getCla() {

		return cla;
	}

	/**
	 * 拿到所有的 字段 包括 @transient
	 * 
	 * @return the allFields
	 */
	public List<FieldConfig> getAllFields() {
		return allFields;
	}

	/**
	 * @return the countTableSql
	 */
	public String getCountTableSql() {
		return countTableSql;
	}

	/**
	 * @return the constructor
	 */
	public Constructor<?> getConstructor() {
		return constructor;
	}

	/**
	 * @param constructor the constructor to set
	 */
	public void setConstructor(Constructor<?> constructor) {
		this.constructor = constructor;
	}

	/**
	 * @param cla the cla to set
	 */
	public void setCla(Class<?> cla) {
		this.cla = cla;
	}

	/**
	 * @return the fields
	 */
	public Map<String, FieldConfig> getFields() {
		return fields;
	}

	/**
	 * 获取字段名字
	 * 
	 * @return
	 */
	public String getFieldColumnNames() {
		StringBuilder sb = new StringBuilder();
		for (FieldConfig fieldConfig : fieldlist) {
			sb.append(fieldConfig.getColumn() + ",");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * @return the selectTableSql
	 */
	public String getSelectTableSql(String field, boolean isAll) {
		if (field != null) {
			return "SELECT " + field + selectTableSql.substring(fromLenth);
		}
		if (isAll) {
			return allSelects;
		}
		return selectTableSql;
	}

	/**
	 * @return the delSql
	 */
	public String getDelSql() {
		return delSql;
	}

	/**
	 * @return the allSelects
	 */
	public String getAllSelects() {
		return allSelects;
	}

	/**
	 * @return the tableAsAll
	 */
	public String getTableAsAll() {
		return tableAsAll;
	}

	/**
	 * @return the joins
	 */
	public List<JoinConfig> getJoins() {
		return joins;
	}

	/**
	 * @param joins the joins to set
	 */
	public void setJoins(List<JoinConfig> joins) {
		this.joins = joins;
	}

	/**
	 * @return the fieldlist
	 */
	public List<FieldConfig> getFieldlist() {
		return fieldlist;
	}

	/**
	 * @return the tableAs
	 */
	public String getTableAs() {
		return tableAs;
	}

	public static String getBeanAsName(Class<?> entiry) {
		return entiry.getSimpleName();
	}

	public List<FieldConfig> getRowMappingList() {
		return rowMappingList == null ? fieldlist : rowMappingList;
	}

	/**
	 * @param rowMappingList the rowMappingList to set
	 */
	public void setRowMappingList(List<FieldConfig> rowMappingList) {
		this.rowMappingList = rowMappingList;
	}

}
