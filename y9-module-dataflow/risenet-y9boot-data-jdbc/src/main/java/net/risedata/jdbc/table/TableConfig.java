package net.risedata.jdbc.table;

import java.util.List;


/**
 * 表的配置信息
 * @author libo
 *2020年12月10日
 */
public class TableConfig {
    private String tableName;
    private List<TableField> fields;
    public static final String TABLE_AS = "t";
    
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
	/**
	 * @return the fields
	 */
	public List<TableField> getFields() {
		return fields;
	}
	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<TableField> fields) {
		this.fields = fields;
	}
	@Override
	public String toString() {
		return "TableConfig [tableName=" + tableName + ", fields=" + fields + "]";
	}
    
    
}
