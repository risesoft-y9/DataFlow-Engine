package net.risedata.jdbc.repository.parse.handles.method.set;

import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.parse.handles.method.InstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;

/**
 * set
 * 
 * @author lb
 * @date 2023年3月15日 上午10:34:51
 */
public class SetInstructionHandle implements InstructionHandle {

	@Override
	public String handleInstruction() {
		return "Set";
	}

	@Override
	public void handle(MethodNameBuilder builder, ClassBuild classBuild) {
		String field = builder.nextAndReturn().next(false, null);
		builder.appendSql(" set " + builder.getColumn(field) + " = " + builder.getParameterArgs(field));
		while (builder.hasNext() && builder.perviewNext().equals("And")) {
			field = builder.nextAndReturn().next(false, null);
			builder.nextAndReturn().appendSql(" , " + builder.getColumn(field) + " = " + builder.getParameterArgs(field));
		}
	}

}
