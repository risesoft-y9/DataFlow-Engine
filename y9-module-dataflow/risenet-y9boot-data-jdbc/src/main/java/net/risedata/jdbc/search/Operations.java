package net.risedata.jdbc.search;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.operation.SqlDefaultOperation;
import net.risedata.jdbc.operation.impl.DefaultOperation;
import net.risedata.jdbc.operation.impl.EQOperation;
import net.risedata.jdbc.operation.impl.InOperation;
import net.risedata.jdbc.operation.impl.LikeOperation;
import net.risedata.jdbc.operation.impl.NullOperation;
/**
 * 包含着一些静态的类
 * @author libo
 *2021年2月9日
 */
public class Operations {
	
	/**
    * like
    */
	public static final Operation LIKE =  new LikeOperation(true);
	public static final Operation NOTLIKE =  new LikeOperation(false);
	/**
	 * 等于
	 */
	public static final Operation EQ =  new DefaultOperation("=");
	/**
	 * 不等于
	 */
	public static final Operation NOTEQ =  new DefaultOperation("!=");
	/**
	 * 大于
	 */
	public static final Operation GT =  new DefaultOperation(">");
	/**
	 * 小于
	 */
	public static final Operation LT =  new DefaultOperation("<");
	/**
	 * 大于等于
	 */
	public static final Operation GTANDEQ =  new DefaultOperation(">=");
	/**
	 * 小于等于
	 */
	public static final Operation LTANDGT =  new DefaultOperation("<=");//小于等于
	/**
	 * in
	 */
	public static final Operation IN = new InOperation(true);
	/**
	 * not in
	 */
	public static final Operation NOTIN = new InOperation(false);
	/**
	 * 占位符 此operation 不做任何操作只是占位
	 */
	public static final Operation PLACEHOLDER = new NullOperation();
	
	public static final List<SqlDefaultOperation> SQL_OPERATIONS = new ArrayList<SqlDefaultOperation>();
	static {
		SQL_OPERATIONS.add((SqlDefaultOperation)LIKE);
		SQL_OPERATIONS.add(new EQOperation());
	}
	
	@NotNull
	public static Operation getSqlTypeDefaultOperation(String type) {
		for (SqlDefaultOperation sqlDefaultOperation : SQL_OPERATIONS) {
			if (sqlDefaultOperation.hasOperation(type)) {
				return sqlDefaultOperation.getOperation(type);
			}
		}
		return null;
	}
}
