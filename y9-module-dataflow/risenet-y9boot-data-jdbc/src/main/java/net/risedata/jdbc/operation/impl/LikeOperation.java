package net.risedata.jdbc.operation.impl;

import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.operation.SqlDefaultOperation;
/**
 * 操作符 in 的实现 
 * @author libo
 *2020年10月9日
 */
public class LikeOperation extends SimpleOperation implements SqlDefaultOperation{
	private String in = ">";//占位符

	public LikeOperation(boolean b) {
 	    if (!b) {
			in=" < ";
		}
	}

	/**
     * 不为空则进行拼接 使用占位符? 的形式凭借最后传递参数交由jdbctemplate 实现
     */
	@Override
	public boolean where(FieldConfig field, List<Object> args,StringBuilder sql
			,Map<String, Object> valueMap,BeanConfig bc,Map<String, Object> excludeMap) {
	    Object ovalue = getValue( field, valueMap);
	   
		if (isNotNull(ovalue)) {
			args.add(ovalue);
			sql.append(" instr("+field.getColumn()+","+ "?) "+in+" 0 ");
			return true;
		}
		return false;
	}



	@Override
	public String toString() {
		return "likeOperation [like=" + in  + "]";
	}



	@Override
	public int getOperate() {
		return 6;
	}

	@Override
	public boolean hasOperation(String sqlType) {
		String type = sqlType.toUpperCase();
		return type.indexOf("CHAR")!=-1||type.indexOf("BLOB")!=-1;
	}

	@Override
	public Operation getOperation(String sqlType) {
		return this;
	}





}
