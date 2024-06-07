package net.risedata.jdbc.repository.parse.handles.method.where.condition;

import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;

/**
 * 区间 填充参数 为字段 以及字段后面哪个字段
 * 
 * @author lb
 * @date 2023年3月14日 上午11:34:30
 */
public class StartingWithInstructionHandle extends ConditionInstructionHandleAdapter {

	public StartingWithInstructionHandle() {
		super("StartingWith", null);
	}

	@Override
	public void handle(MethodNameBuilder builder, String backField, String condition) {
		int index = builder.getParameterIndex(backField);
		builder.appendBody("$"+index+"+=\"%\";");
		builder.getSqlbuilder().where(builder.getColumn(backField) + " like " + builder.getParameterArgs(backField));
	}

}
