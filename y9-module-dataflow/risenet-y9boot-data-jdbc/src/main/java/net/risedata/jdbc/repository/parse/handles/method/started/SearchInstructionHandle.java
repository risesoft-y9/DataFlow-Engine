package net.risedata.jdbc.repository.parse.handles.method.started;

import net.risedata.jdbc.exception.ProxyException;
import net.risedata.jdbc.factory.BeanConfigFactory;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.parse.handles.SearchParseHandle;
import net.risedata.jdbc.repository.parse.handles.method.InstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.InstructionManager;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;
import net.risedata.jdbc.repository.parse.handles.method.StartedInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.order.OrderInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.section.SectionInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.WhereInstructionHandleManager;

public class SearchInstructionHandle implements StartedInstructionHandle {

	@Override
	public String handleInstruction() {
		return "find";
	}

	@Override
	public void handle(MethodNameBuilder builder, ClassBuild classBuild) {
		builder.nextAndReturn().appendSql("select ");
		if (InstructionManager.hasInstruction(builder.perviewNext())) {
			if (InstructionManager.hasInstruction(SectionInstructionHandle.class, builder.perviewNext())) {
				InstructionManager.get(SectionInstructionHandle.class, builder.perviewNext(), true).handle(builder,
						classBuild);
			} else {
				builder.appendSql(BeanConfigFactory.getInstance(classBuild.getGenericityClass()).getFieldColumnNames())
						.appendSql(" ");
			}
		} else {
			StringBuilder select = new StringBuilder(builder.getColumn(builder.next()));
			while (!InstructionManager.hasInstruction(builder.perviewNext())) {
				select.append(",").append(builder.getColumn(builder.next()));
			}
			builder.appendSql(select.toString()).appendSql(" ");
		}
		builder.appendSql(" from " + BeanConfigFactory.getTableName(classBuild.getGenericityClass()));
		WhereInstructionHandleManager.handle(builder, classBuild);
		InstructionHandle instructionHandle = InstructionManager.get(builder.perviewNext());
		if (instructionHandle instanceof OrderInstructionHandle) {
			InstructionManager.get(builder.perviewNext()).handle(builder, classBuild);
		}
		if (builder.hasNext()) {
			throw new ProxyException("无效的标识符:" + builder.perviewNext());
		}
		builder.appendBody(SearchParseHandle.parseBody(builder.getMethod(), builder.getReturnType(), classBuild,
				builder.getSqlbuilder().toString()));

	}

}
