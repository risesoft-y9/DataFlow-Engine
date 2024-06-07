package net.risedata.jdbc.repository.parse.handles.method.where.condition;

import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;

/**
 * 区间 填充参数 为字段 以及字段后面哪个字段
 * 
 * @author lb
 * @date 2023年3月14日 上午11:34:30
 */
public class FalseInstructionHandle extends ConditionInstructionHandleAdapter {

	public FalseInstructionHandle() {
		super("False", null);
	}

	@Override
	public void handle(MethodNameBuilder builder, String backField, String condition) {
		builder.getSqlbuilder().where(builder.getColumn(backField) + " = False ");
	}

}
