package net.risedata.jdbc.operation;

import net.risedata.jdbc.search.Operations;

/**
 * 基础操作集合枚举类型
 * 
 * @author libo 2020年11月30日
 */
public enum Operates {

	LIKE(Operations.LIKE), NOTLIKE(Operations.NOTLIKE), EQ(Operations.EQ), NOTEQ(Operations.NOTEQ), LT(Operations.LT),
	GTANDEQ(Operations.GTANDEQ), LTANDGT(Operations.LTANDGT), IN(Operations.IN), NOTIN(Operations.NOTIN),
	GT(Operations.GT)
	/**
	 * 占位符
	 */
	,PLACEHOLDER(Operations.PLACEHOLDER);

	public Operation value;
	public Class<? extends Operation> cla;

	Operates(Operation ot) {
		this.value = ot;
		this.cla = ot.getClass();
	}
};