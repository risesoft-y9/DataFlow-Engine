package net.risedata.jdbc.repository.parse.handles.search;

import java.util.Map;

import net.risedata.jdbc.repository.model.ArgsBuilder;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.repository.proxy.RepositoryCreateFactory;

public class MapSearchReturnHandle implements SearchReturnHandle {
	private final String CODE = "return " + RepositoryCreateFactory.JDBC_TEMPLATE + ".queryForMap(_sql";

	@Override
	public boolean isHandle(ReturnType type) {
		return type.getReturnType() == Map.class;
	}

	@Override
	public String parse(ReturnType type, ClassBuild properties,ArgsBuilder argsBuilder) {
		return CODE+argsBuilder.to()+");";
	}

}
