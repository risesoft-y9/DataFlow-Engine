package net.risedata.jdbc.repository.parse.handles.method.where;

import net.risedata.jdbc.exception.ProxyException;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;

public class AndInstructionHandle implements WhereInstructionHandle {

	@Override
	public String handleInstruction() {
		return "And";
	}

	@Override
	public void handle(MethodNameBuilder builder, ClassBuild classBuild) {

		throw new ProxyException("And不能单独执行");
	}

}
