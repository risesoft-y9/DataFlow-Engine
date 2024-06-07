package net.risedata.jdbc.repository.parse.handles.method.where.condition;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.risedata.jdbc.exception.ProxyException;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;

public class SimpledInstructionHandle implements ConditionInstructionHandle {

	private static Map<String, String> conditionMap = new HashMap<String, String>();
	static {
		conditionMap.put("Equals", "=");
		conditionMap.put("Is", "=");
		conditionMap.put("LessThan", "<");
		conditionMap.put("LessThanEqual", "<=");
		conditionMap.put("GreaterThan", ">");
		conditionMap.put("GreaterThanEqual", ">=");
		conditionMap.put("Like", "like");
		conditionMap.put("NotLike", "not like");
		conditionMap.put("In", "in ");
		conditionMap.put("NotIn", "not in");
		conditionMap.put("Not", "<>");
	}

	@Override
	public String handleInstruction() {
		return StringUtils.join(conditionMap.keySet(), ",");
	}

	@Override
	public void handle(MethodNameBuilder builder, ClassBuild classBuild) {
		throw new ProxyException("Condition no handle");

	}

	@Override
	public void handle(MethodNameBuilder builder, String backField, String condition) {
		
		builder.getSqlbuilder()
				.where(builder.getColumn(backField) + " " + conditionMap.get(condition) + " ?" + builder.getParameterIndex(backField)+" ");
	}

}
