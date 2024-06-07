package risesoft.data.transfer.stream.es.utils;


/**
 * 排序
 * 
 * @author libo 2020年10月10日
 */
public class Order implements Comparable<Order> {
	/**
	 * 升序
	 */
	public static final String DESC = "DESC";
	/**
	 * 降序
	 */
	public static final String ASC = "ASC";

	private String expression;// 表达式
	private String field;
	private int level = 0;

	public Order(String field, String order) {
		this.field = field;
		this.order = order;
	}

	public Order(String field, String order, int level) {
		this(field, order);
		this.level = level;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

	private String order;

	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public String toString() {
		return "Order [field=" + field + ", level=" + level + ", order=" + order + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Order) {
			return this.field.equals(((Order) obj).field);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.field.hashCode();
	}

	@Override
	public int compareTo(Order o) {
		return this.level - o.level;
	}

}
