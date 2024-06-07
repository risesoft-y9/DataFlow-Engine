package net.risedata.jdbc.operation;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.risedata.jdbc.condition.Condition;
import net.risedata.jdbc.factory.ConditionProxyFactory;

/**
 * 封装操作
 * @author libo
 *2020年11月30日
 */
public class OperationPack {

     /**
      * 条件执行器
      */
     private Condition condition;
     /**
      * 操作
      */
     private Operation operation;
    
     private String expression;
     
     public void init(Class<?> bean) {
    	 if (StringUtils.isNotBlank(expression)) {
			this.condition = ConditionProxyFactory.getInstance(expression, bean);
		}
     }
     public OperationPack(String expression, Operation operation) {
		this.expression = expression;
		this.operation = operation;
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
	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

   /**
    * 条件是否成立
    * @param valueMap
    * @return
    */
	public boolean condition(Map<String, Object> valueMap) {
   	 return this.condition==null?true:this.condition.isHandle(valueMap);
    }




	@Override
	public String toString() {
		return "OperationPack [ex=" + expression + ", operation=" + operation + "]";
	}
     


}
