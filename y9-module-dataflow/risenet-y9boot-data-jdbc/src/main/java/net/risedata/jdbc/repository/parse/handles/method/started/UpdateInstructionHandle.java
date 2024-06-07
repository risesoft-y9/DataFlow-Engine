package net.risedata.jdbc.repository.parse.handles.method.started;

import net.risedata.jdbc.exception.ProxyException;
import net.risedata.jdbc.factory.BeanConfigFactory;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.parse.handles.UpdateParseHandle;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;
import net.risedata.jdbc.repository.parse.handles.method.StartedInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.WhereInstructionHandleManager;

public class UpdateInstructionHandle implements StartedInstructionHandle {

	@Override
	public String handleInstruction() {
		return "update";
	}

	@Override
	public void handle(MethodNameBuilder builder, ClassBuild classBuild) {
		builder.nextAndReturn().appendSql("update " + BeanConfigFactory.getTableName(classBuild.getGenericityClass()));
		WhereInstructionHandleManager.handle(builder, classBuild);
		if (builder.hasNext()) {
			throw new ProxyException("无效的标识符:" + builder.perviewNext());
		}
		builder.appendBody(UpdateParseHandle.parseBody(builder.getMethod(), builder.getReturnType(), classBuild,
				builder.getSqlbuilder().toString()));

	}

}
