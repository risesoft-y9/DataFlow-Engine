package net.risedata.jdbc.repository.parse.handles.method.order;

import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.parse.handles.method.InstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;

/**
 * 排序操作
 * 
 * @author lb
 * @date 2023年3月15日 上午9:58:22
 */
public class OrderInstructionHandle implements InstructionHandle {

	@Override
	public String handleInstruction() {
		return "Order,Desc,Asc";
	}

	@Override
	public void handle(MethodNameBuilder builder, ClassBuild classBuild) {
		builder.nextAndReturn().appendSql(
				" order by " + builder.getColumn(builder.next(false, null)) + " " + builder.next(true, "Desc"));
		while (builder.hasNext() && builder.perviewNext().equals("And")) {
			builder.nextAndReturn()
					.appendSql(" , " + builder.getColumn(builder.next(false, null)) + " " + builder.next(true, "Desc"));
		}
	}

}
