package net.risedata.jdbc.operation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.search.exception.FieldException;

/**
 * 括号操作实现 注意 此括号操作需要指定在某个字段之后执行此括号操作 当 此括号存在于or中or需要指定括号不需要
 * 
 * 
 * @author libo
 *2020年10月12日
 */
public class BracketsOperation  extends SimpleOperation{
	/**
	 * 操作集合
	 */
    private List<Operation> operations = new ArrayList<Operation>();
    /**
     * key集合
     */
    private List<String> keys = new ArrayList<>();
	public BracketsOperation() {

	}
	
	public BracketsOperation(String key,Operation operation) {
		add(key, operation);
	}
	/**
	 * 根据key 添加操作
	 * @param key
	 * @param operation
	 * @return
	 */
	public BracketsOperation add(String key,Operation operation) {
		keys.add(key);
		operations.add(operation);
		return this;
	}
	@Override
	public boolean where(FieldConfig field, List<Object> args, StringBuilder sql,
			Map<String, Object> valueMap,BeanConfig bc,Map<String, Object> excludeMap) {
		sql.append("(");
		FieldConfig fc=null;
		boolean flag = true;
		for (int i = 0; i < operations.size(); i++) {
			 fc = bc.getField(keys.get(i));
			 if (fc != null) {
				 if(operations.get(i).where( fc, args, sql, valueMap, bc,excludeMap)) {
					 if (flag) {
						 flag = false;
					 }else {
						 sql.append(" and ");
					 }
				 }
				continue;
			 }
			 throw new FieldException(keys.get(i)+"不存在此字段映射");
		}
		return false;
	}
	@Override
	public int getOperate() {
		return 0;
	}
}
