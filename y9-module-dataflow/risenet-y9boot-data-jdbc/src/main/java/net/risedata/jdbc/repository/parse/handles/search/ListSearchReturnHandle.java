package net.risedata.jdbc.repository.parse.handles.search;

import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.factory.BeanConfigFactory;
import net.risedata.jdbc.repository.model.ArgsBuilder;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.repository.proxy.RepositoryCreateFactory;

public class ListSearchReturnHandle implements SearchReturnHandle {

	@Override
	public boolean isHandle(ReturnType type) {
		return type.getReturnType() == List.class;
	}

	@Override
	public String parse(ReturnType type, ClassBuild properties, ArgsBuilder argsBuilder) {
		if (type.isClass()) {
			BeanConfig bc = BeanConfigFactory.getInstance(type.getGenericityClass());
			if (bc == null) {
				if (type.getGenericityClass() == Map.class) {
					return "return " + RepositoryCreateFactory.JDBC_TEMPLATE + ".queryForListMap(_sql" + argsBuilder.to()
							+ ");";
				}
				return "return " + RepositoryCreateFactory.JDBC_TEMPLATE + ".queryForSimpleList(_sql, "
						+ type.getGenericityClass().getName() + ".class" + argsBuilder.to() + ");";
			} else {
				return "return " + RepositoryCreateFactory.JDBC_TEMPLATE + ".queryForList(_sql, "
						+ type.getGenericityClass().getName() + ".class " + argsBuilder.to() + ");";
			}
		} else {
			argsBuilder.delete(type.getIndex());
			return "return " + RepositoryCreateFactory.JDBC_TEMPLATE + ".queryForList(_sql,(java.lang.Class)$args["
					+ type.getIndex() + "]" + argsBuilder.to() + ");";
		}
	}

}
