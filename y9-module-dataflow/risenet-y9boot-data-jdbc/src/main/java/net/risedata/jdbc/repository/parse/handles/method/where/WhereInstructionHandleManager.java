package net.risedata.jdbc.repository.parse.handles.method.where;

import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.parse.handles.method.InstructionManager;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;

/**
 * 管理where
 * 
 * @author lb
 * @date 2023年3月14日 上午10:31:56
 */
public class WhereInstructionHandleManager {

	public static void handle(MethodNameBuilder builder, ClassBuild classBuild) {
		if (InstructionManager.hasInstruction(WhereInstructionHandle.class,builder.perviewNext())) {
			InstructionManager.get(WhereInstructionHandle.class, builder.next(), true).handle(builder, classBuild);
		}
	}

}
