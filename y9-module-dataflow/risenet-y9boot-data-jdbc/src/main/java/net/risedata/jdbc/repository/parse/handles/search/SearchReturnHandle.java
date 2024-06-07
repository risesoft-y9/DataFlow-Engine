package net.risedata.jdbc.repository.parse.handles.search;

import net.risedata.jdbc.repository.model.ArgsBuilder;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.model.ReturnType;

public interface SearchReturnHandle {
	
	
	
	boolean isHandle(ReturnType type);

	String parse(ReturnType type,ClassBuild properties,ArgsBuilder argsBuilder);

}
