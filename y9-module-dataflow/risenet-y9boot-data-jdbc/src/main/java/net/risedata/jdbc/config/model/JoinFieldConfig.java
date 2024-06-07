package net.risedata.jdbc.config.model;

/**
 * 连接查询的配置 只支持简单的连接查询
 * @author libo
 *2020年10月19日
 */

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * join 里面 每一个字段的config
 * 
 * @author libo 2020年10月19日
 */
public class JoinFieldConfig {
	/**
	 * 字段配置用于 cloum and mapping
	 */
	private FieldConfig fc;
	private List<FieldConfig> fields;// entiry对象独有 只调用entiry
	private boolean isAutoFunction;// 是否不自动拼接方法针对聚合函数
	private String function;// 方法

	/**
	 * @return the isAutoFunction
	 */
	public boolean isAutoFunction() {
		return isAutoFunction;
	}

	/**
	 * @param isAutoFunction the isAutoFunction to set
	 */
	public void setAutoFunction(boolean isAutoFunction) {
		this.isAutoFunction = isAutoFunction;
	}

	/**
	 * @return the function
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * @param function the function to set
	 */
	public void setFunction(String function) {
		this.function = function;
	}

	/**
	 * @return the fields
	 */
	public List<FieldConfig> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<FieldConfig> fields) {
		this.fields = fields;
	}

	/**
	 * @return the fc
	 */
	public FieldConfig getFc() {
		return fc;
	}

	/**
	 * @param fc the fc to set
	 */
	public void setFc(FieldConfig fc) {
		this.fc = fc;
	}

	/**
	 * 自己的字段 通常写在注解上 对应的映射字段
	 */
	private String column;
	/**
	 * 也就是最后reset的get 的字段 名
	 */
	private String returnCol;

	/**
	 * @return the column
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * 根据 配置将自己的sql 初始化出来 index 为配置的index
	 */
	public void toSql(StringBuilder sql, String tableAs, String tableName) {
		if (StringUtils.isBlank(function)) {
			if (!this.fc.isEntiry()) {// 非entiry对象的情况
				this.returnCol = tableAs + "_" + this.column;
				if (sql.indexOf(returnCol) == -1) {
					sql.append(this.column + " as " + returnCol);
				}
				fc.setColumn(returnCol);
			} else {// join 连接的字段是 一个实体类需要取出整个实体类的情况下
				for (int i = 0; i < this.fields.size(); i++) {// 添加一大堆as 这些as都是一个一个的配置
					this.returnCol = tableAs + "_" + fields.get(i).getColumn();
					if (sql.indexOf(returnCol) == -1) {
						sql.append(" " + fields.get(i).getColumn() + " as " + returnCol + " , ");
					}
					fields.get(i).setColumn(this.returnCol);
				}
			}
		} else {// function 则追加到 where 中
			this.returnCol = tableAs + "_" + this.column;
			if (isAutoFunction) {// 不需要我拼接的
				sql.append("(" + this.function + ")" + this.returnCol);
			} else {
				sql.append("( SELECT " + function + "(" + this.column + ") FROM " + tableName + " " + tableAs
						+ " where " + JoinConfig.IDRELATION + ")" + this.returnCol);
			}
			fc.setColumn(this.returnCol);
		}
	}

	/**
	 * @return the returnCol
	 */
	public String getReturnCol() {
		return returnCol;
	}

	public void toAs(StringBuilder sb, String as, String tableName) {
		if (fc.isEntiry()) {// 遍历fields获取
			for (int i = 0; i < fields.size(); i++) {

				sb.append(fields.get(i).getColumn());
				if (i != fields.size() - 1) {
					sb.append(",");
				}
			}
		} else {
			sb.append(fc.getColumn());
		}

	}

	@Override
	public String toString() {
		return "JoinFieldConfig [fc=" + fc + ", fields=" + fields + ", isAutoFunction=" + isAutoFunction + ", function="
				+ function + ", column=" + column + ", returnCol=" + returnCol + "]";
	}

}
