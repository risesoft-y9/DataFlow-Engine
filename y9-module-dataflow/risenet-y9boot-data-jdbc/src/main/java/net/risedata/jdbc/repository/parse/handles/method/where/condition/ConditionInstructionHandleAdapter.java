package net.risedata.jdbc.repository.parse.handles.method.where.condition;

import net.risedata.jdbc.exception.ProxyException;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;

public class ConditionInstructionHandleAdapter implements ConditionInstructionHandle {

	private String condition;

	private String conditionKey;

	/**
	 * @param condition
	 * @param conditionKey
	 */
	public ConditionInstructionHandleAdapter(String conditionKey, String condition) {
		this.condition = condition;
		this.conditionKey = conditionKey;
	}

	@Override
	public String handleInstruction() {
		return conditionKey;
	}

	@Override
	public void handle(MethodNameBuilder builder, ClassBuild classBuild) {
		throw new ProxyException("Condition no handle");

	}

	@Override
	public void handle(MethodNameBuilder builder, String backField, String condition) {
		builder.getSqlbuilder()
				.where(builder.getColumn(backField) + " " + this.condition + builder.getParameterArgs(backField));
	}

}
