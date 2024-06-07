package net.risedata.jdbc.repository.parse.handles.method.where;

import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.parse.handles.method.InstructionManager;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;
import net.risedata.jdbc.repository.parse.handles.method.where.condition.ConditionInstructionHandle;

public class ByInstructionHandle implements WhereInstructionHandle {

	@Override
	public String handleInstruction() {
		return "By";
	}

	@Override
	public void handle(MethodNameBuilder builder, ClassBuild classBuild) {
		
		while (builder.hasNext() && !builder.perviewNext().equals("Order")) {
			doOperation(builder);
			if ("And".equals(builder.perviewNext()) || "Or".equals(builder.perviewNext())) {
				doOperation(builder.nextAndReturn());
			}
		}
	}

	private void doOperation(MethodNameBuilder builder) {
		String field = builder.next(false, null);
		String operation = "And".equals(builder.perviewNext()) ? "Equals" : builder.next(true, "Equals");

		((ConditionInstructionHandle) InstructionManager.get(ConditionInstructionHandle.class, operation, true))
				.handle(builder, field, operation);

	}

}
