package net.risedata.jdbc.repository.parse.handles.method.where.condition;

import net.risedata.jdbc.repository.parse.handles.method.InstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;

public interface ConditionInstructionHandle extends InstructionHandle {
	/**
	 * 执行handle
	 * 
	 * @param builder
	 */
	void handle(MethodNameBuilder builder, String backField, String condition);
}
