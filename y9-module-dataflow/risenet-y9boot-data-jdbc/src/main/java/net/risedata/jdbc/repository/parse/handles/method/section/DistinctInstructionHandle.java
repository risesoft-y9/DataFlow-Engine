package net.risedata.jdbc.repository.parse.handles.method.section;

import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;

/**
 * 动态使用字段操作
 * 
 * @author lb
 * @date 2023年3月13日 下午3:45:31
 */
public class DistinctInstructionHandle implements SectionInstructionHandle {

	@Override
	public String handleInstruction() {
		return "$distinct";
	}

	@Override
	public void handle(MethodNameBuilder builder, ClassBuild classBuild) {
		builder.nextAndReturn().appendSql(" distinct " + builder.next(false, null));
	}

}
