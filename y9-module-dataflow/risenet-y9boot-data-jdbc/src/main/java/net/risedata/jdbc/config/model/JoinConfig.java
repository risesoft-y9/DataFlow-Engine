package net.risedata.jdbc.config.model;

/**
 * 连接其他表查询的config
 * @author libo
 *2020年10月19日
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import net.risedata.jdbc.condition.Condition;
import net.risedata.jdbc.search.exception.JoinException;

/**
 * 配置与一个table 的join
 * 
 * @author libo 2020年10月20日
 */
public class JoinConfig {
	/**
	 * 所连接主表的名字会在生成的连接sql中替换
	 */
	public static final String MAIN_TABLE_NAME = "$table";
	private Class<?> tableClass;
	private Condition condition;

	/**
	 * join的表名
	 */
	private String tableName;
	/**
	 * 给表存在的where
	 */
	private String where;

	/**
	 * 连接的条件 对方的id 会 通过反射加载到id
	 */
	private String[] toId;
	/**
	 * 自己与他连接的joinId
	 */
	private String[] joinId;
	/**
	 * 内部链接字段里面的fieldconfig
	 */
	private List<FieldConfig> fields;
	/**
	 * 自己表的别名
	 */
	private String as;
	/**
	 * 最后的sql
	 */
	private String sql;
	/**
	 * join 的 bean
	 */
	private BeanConfig joinBean;
	/**
	 * 自己的beanconfig
	 */
	private BeanConfig myBean;
	/**
	 * 字段的sql 只有在连接字段的时候才有效
	 */
	private String fieldSql;

	/**
	 * 聚合函数 模式表示非一一对应
	 */
	private boolean isFunction = false;
	/**
	 * 内部链接的字段配置
	 */
	private List<JoinFieldConfig> fieldConfigs = new ArrayList<JoinFieldConfig>();

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	public void addField(JoinFieldConfig field) {
		this.fieldConfigs.add(field);
		boolean flag = false;
		for (JoinFieldConfig joinFieldConfig : fieldConfigs) {
			if (StringUtils.isBlank(joinFieldConfig.getFunction())) {
				flag = true;
				if (this.isFunction) {// 是否为function 模式
					throw new JoinException(myBean + " 非一对一关系以及配置了聚合关系");
				}
			} else {// 不为空的function
				this.isFunction = true;
				if (flag) {// 如果存在不是function的
					throw new JoinException(myBean + " 非一对一关系以及配置了聚合关系");
				}
			}
		}

	}

	/**
	 * @param where the where to set
	 */
	public void setWhere(String where) {
		if (this.where == null) {
			this.where = where;
		} else {
			throw new JoinException("表已存在where 条件");
		}
	}

	public static final String IDRELATION = "$idrelation";// 在 function 模式下最后的 此字符串都会转为 id的映射关系

	/**
	 * 初始化自己的sql
	 * 
	 * @param tableAs  自己的别名
	 * @param parentAs 上级bean的 别名
	 */
	public void toSql(String tableAs, String parentAs) {
		if (joinId != null && joinId.length != toId.length) {
			throw new JoinException("joinid and to id required corresponding joinid length = " + joinId.length
					+ "toid lenght = " + toId.length);
		}

		StringBuilder sb = null;
		if (isFunction) {
			sb = new StringBuilder();
		} else {
			sb = new StringBuilder(" LEFT JOIN (SELECT ");
		}
		this.as = tableAs;// t0 t1 tx
		this.fields = new ArrayList<>();
		StringBuilder fieldsSql = new StringBuilder();
		for (int i = 0; i < fieldConfigs.size(); i++) {
			fieldConfigs.get(i).toSql(sb, as, tableName);

			if (fieldConfigs.get(i).getFc().isEntiry()) {// entiry
				this.fields.add(fieldConfigs.get(i).getFc());
			} else {
				this.fields.add(fieldConfigs.get(i).getFc());
				sb.append(",");
			}
			fieldConfigs.get(i).toAs(fieldsSql, as, tableName);
		}
		this.fieldSql = fieldsSql.toString();

		if (!isFunction) {// 非聚合
			FieldConfig[] toFields = new FieldConfig[toId.length];
			String[] fieldCol = new String[toId.length];// 保存最后as_的名字
			FieldConfig[] joinFields = new FieldConfig[joinId.length];
			boolean flag = false;
			for (int i = 0; i < toId.length; i++) {
				toFields[i] = joinBean.getField(toId[i]);
				joinFields[i] = myBean.getField(joinId[i]);
				if (toFields[i] == null) {
					throw new JoinException("join field:" + toId[i] + " join bean no field");
				}
				if (joinFields[i] == null) {
					throw new JoinException(" field: " + joinId[i] + " bean no field");
				}
				fieldCol[i] = as + "_" + toFields[i].getColumn();
				if (sb.indexOf(fieldCol[i]) == -1) {// id 已经被使用则无视此id

					sb.append(toFields[i].getColumn() + " as " + fieldCol[i]);
					if (i != toId.length - 1) {
						sb.append(",");

					}
					flag = true;
				}
			}
			if (!flag) {// 删掉最后的, 因为没有追加id
				sb.delete(sb.length() - 2, sb.length());
			} // 拼接连接条件
			sb.append(" from " + tableName + (this.where == null ? "" : " where " + where) + ") " + as + " on "
					+ (joinId.length > 1 ? "(" : ""));
			for (int i = 0; i < joinId.length; i++) {
				sb.append(as + "." + fieldCol[i] + "=" + parentAs + "." + joinFields[i].getColumn());
				if (i != joinId.length - 1) {
					sb.append(" and ");
				}
			}
			if (joinId.length > 1) {
				sb.append(") ");
			}
			sql = sb.toString();
		} else {// 聚合模式
				// sb中已经将 每个聚合函数都拼接好了 这里只需要加载出table id 的关系即可
			if (toId == null && joinId == null) {// 自定义的聚合
				sql = sb.toString();
				return;
			}
			StringBuilder idBuilder = new StringBuilder();
			FieldConfig[] toFields = new FieldConfig[toId.length];
			FieldConfig[] joinFields = new FieldConfig[joinId.length];
			for (int i = 0; i < toId.length; i++) {// 加载每个field的配置
				try {

					toFields[i] = joinBean.getField(toId[i]);
				} catch (Exception e) {
					System.out.println(joinBean);
					
				}
				joinFields[i] = myBean.getField(joinId[i]);
				if (toFields[i] == null) {
					throw new JoinException("join field" + toId[i] + " join bean no field");
				}
				if (joinFields[i] == null) {
					throw new JoinException(" field" + joinId[i] + " bean no field");
				}

				idBuilder.append(
						as + "." + toFields[i].getColumn() + " = " + parentAs + "." + joinFields[i].getColumn());
				if (i != joinId.length - 1) {
					idBuilder.append(" and ");
				}
			}
			sql = sb.toString().replace(IDRELATION, idBuilder.toString()+(where!=null?(" and "+where):""));
		}
		sql=sql.replace(MAIN_TABLE_NAME, myBean.getTableAs());
	}

	/**
	 * 拿到当前连接表的别名
	 * 
	 * @return
	 */
	public String getTableAs() {
		return as;
	}

	/**
	 * 根据 字段名拿到字段的别名
	 * 
	 * @param field
	 * @return
	 */
	public String getFieldAsForField(String field) {
		FieldConfig fieldConfig = joinBean.getField(field);
		if (fieldConfig != null) {
			return getTableAs() + "_" + fieldConfig.getColumn();
		}
		throw new NullPointerException(field + " is null config");
	}

	/**
	 * 根据表的字段名拿到别名
	 * 
	 * @param cloum
	 * @return
	 */
	public String getFieldAsForCloum(String cloum) {

		return getTableAs() + "_" + cloum;

	}

	/**
	 * @return the condition
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public boolean isJoin(Map<String, Object> o) {
		return condition == null ? true : condition.isHandle(o);
	}

	/**
	 * @return the myBean
	 */
	public BeanConfig getMyBean() {
		return myBean;
	}

	/**
	 * @param myBean the myBean to set
	 */
	public void setMyBean(BeanConfig myBean) {
		this.myBean = myBean;
	}

	/**
	 * @return the toId
	 */
	public String[] getToId() {
		return toId;
	}

	/**
	 * @param toId the toId to set
	 */
	public void setToId(String[] toId) {
		this.toId = toId;
	}

	/**
	 * @return the joinId
	 */
	public String[] getJoinId() {
		return joinId;
	}

	/**
	 * @param joinId the joinId to set
	 */
	public void setJoinId(String[] joinId) {
		this.joinId = joinId;
	}

	public Collection<? extends FieldConfig> getFields() {

		return this.fields;
	}

	/**
	 * @param isFunction the isFunction to set
	 */
	public void setFunction(boolean isFunction) {
		this.isFunction = isFunction;
	}

	/**
	 * @return the isFunction
	 */
	public boolean isFunction() {
		return isFunction;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @return the joinBean
	 */
	public BeanConfig getJoinBean() {
		return joinBean;
	}

	/**
	 * @param joinBean the joinBean to set
	 */
	public void setJoinBean(BeanConfig joinBean) {
		this.joinBean = joinBean;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		return "JoinConfig [tableClass=" + tableClass + ", condition=" + condition + ", tableName=" + tableName
				+ ", where=" + where + ", toId=" + Arrays.toString(toId) + ", joinId=" + Arrays.toString(joinId)
				+ ", fields=" + fields + ", as=" + as + ", sql=" + sql + ", joinBean=" + joinBean.getCla() + ", myBean="
				+ myBean.getCla() + ", fieldSql=" + fieldSql + ", isFunction=" + isFunction + ", fieldConfigs="
				+ fieldConfigs + "]";
	}

	/**
	 * @return the fieldSql
	 */
	public String getFieldSql() {
		return fieldSql;
	}

	/**
	 * @param fieldSql the fieldSql to set
	 */
	public void setFieldSql(String fieldSql) {
		this.fieldSql = fieldSql;
	}

	/**
	 * @return the tableClass
	 */
	public Class<?> getTableClass() {
		return tableClass;
	}

	/**
	 * @param tableClass the tableClass to set
	 */
	public void setTableClass(Class<?> tableClass) {
		this.tableClass = tableClass;
	}
}
