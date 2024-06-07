package net.risedata.jdbc.repository.parse.handles.search;

import net.risedata.jdbc.repository.model.ArgsBuilder;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.repository.proxy.RepositoryCreateFactory;

public class DefaultSearchReturnHandle implements SearchReturnHandle {
	private final String CODE_PRE = "return ($r)" + RepositoryCreateFactory.JDBC_TEMPLATE + ".queryForObject(_sql,";
	private final String CODE_AFTER = ");";

	@Override
	public boolean isHandle(ReturnType type) {
		return true;
	}

	@Override
	public String parse(ReturnType type, ClassBuild properties,ArgsBuilder argsBuilder) {
		if (type.isClass()) {
			return CODE_PRE + type.getReturnType().getName()+".class"+argsBuilder.to()+ CODE_AFTER;
		} else {
			if (type.isGenericity()) {
				argsBuilder.delete(type.getIndex());
				return CODE_PRE + "(java.lang.Class)$args[" + type.getIndex() + "]"+argsBuilder.to() + CODE_AFTER;
			}
			return CODE_PRE +  type.getReturnType().getName()+".class"+argsBuilder.to()+ CODE_AFTER;
		}
	}

}
