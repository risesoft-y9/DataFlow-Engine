package net.risedata.rpc.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * loaderclass
 * 
 * @author libo 2020年7月15日
 */
@SuppressWarnings("rawtypes")
public class LClassLoader {

	public static List<Class<?>> findClass(String packageName)  {


	
	return	ClassTools.getClasses(packageName);

	}

}