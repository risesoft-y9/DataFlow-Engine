package net.risedata.jdbc.repository.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 参数操作model
 * 
 * @author libo 2021年7月12日
 */
public class ArgsBuilder {

	private int[] deleteArgs;

	private List<String> args;

	private Method m;

	private StringBuilder body;

	private int index;

	public ArgsBuilder(Method m) {
		this.m = m;
	}

	/**
	 * 添加一个参数 注意add 和delete 不能同时存在
	 */
	public void add(String arg) {
		if (args == null) {
			args = new ArrayList<String>();
		}
		args.add(arg);
	}

	/**
	 * 删除一个参数 注意add 和delete 不能同时存在
	 * 
	 * @param index
	 */
	public void delete(int index) {
		if (deleteArgs == null) {
			deleteArgs = new int[m.getParameterCount()];
		}
		deleteArgs[index] = 1;
	}

	/**
	 * 将参数转换出来
	 * 
	 * @return
	 */
	public String to() {
		if (body != null) {
			return "," + ARGS_KEY + ".toArray()";
		}
		if (args != null) {
			return ",new Object[]{" + StringUtils.join(args, ",") + "}";
		}
		if (deleteArgs != null) {
			StringBuilder argsBuilder = new StringBuilder();
			for (int i = 0; i < deleteArgs.length; i++) {
				if (deleteArgs[i] != 1) {
					argsBuilder.append(",$args[" + i + "]");
				}
			}
			if (argsBuilder.length() == 0) {
				return "";
			}
			return ",new Object[]{" + argsBuilder.substring(1, argsBuilder.length()) + "}";
		}
		return ",$args";
	}

	public String toBody() {
		if (body != null) {
			if (args!=null) {				
				for (int i = index; i < args.size(); i++) {
					addArg(args.get(i));
				}
			}
			return body.toString();
		}
		return "";
	}

	public static final String ARGS_KEY = "$Largs";

	private static final String ARGS_INIT = "java.util.List " + ARGS_KEY + " =  new java.util.ArrayList();";

	public ArgsBuilder appendArg(String arg) {
		initArgs();
		addArg(arg);
		return this;
	}

	public ArgsBuilder appendToBody(String b) {
		initArgs();
		body.append(b);
		return this;
	}

	public ArgsBuilder appendAllArg(String arg) {
		initArgs();
		addArgs(arg);
		return this;
	}

	private void initArgs() {
		if (body == null) {
			body = new StringBuilder(ARGS_INIT);
			if (args != null) {
				for (int i = 0; i < args.size(); i++) {
					appendArg(args.get(i));
				}
				index = args.size();
			}
		}
	}

	public void addArg(String arg) {
		body.append(ARGS_KEY).append(".add(").append(arg).append(");");
	}

	public void addArgs(String args) {
		body.append(ARGS_KEY).append(".addAll(").append(args).append(");");
	}

	/**
	 * @return the isDynamic
	 */
	public boolean isDynamic() {
		return body!=null;
	}


}
