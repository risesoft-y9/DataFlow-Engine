package net.risedata.jdbc.repository.parse.handles.method;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

/**
 * 用 方法名来构建sql 的builder
 * @author lb
 * @date 2023年3月13日 上午11:42:01
 */

import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.exception.ProxyException;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.utils.Sqlbuilder;

public class MethodNameBuilder {
	/**
	 * sql
	 */

	private Sqlbuilder sql = new Sqlbuilder();
	private String body = "";
	private List<String> instructions;
	private Method method;
	/**
	 * 当前指令
	 */
	private int current = 0;
	/**
	 * 参数map 对应参数名
	 */
	private Map<Object, ParameterModel> parameterMap = new HashMap<Object, ParameterModel>();

	private BeanConfig beanConfig;

	private ReturnType returnType;

	/**
	 * @param instructions
	 */
	public MethodNameBuilder(List<String> instructions, Parameter[] parameters, BeanConfig beanConfig, Method method,
			ReturnType returnType) {
		this.instructions = instructions;
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].getName().equals("arg" + i)) {
				this.parameterMap.put(parameters[i].getParameterizedType().getTypeName().toLowerCase(),
						new ParameterModel(i + 1, parameters[i]));
			} else {
				this.parameterMap.put(parameters[i].getName(), new ParameterModel(i + 1, parameters[i]));
			}
		}
		this.beanConfig = beanConfig;
		this.method = method;
		this.returnType = returnType;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @return the returnType
	 */
	public ReturnType getReturnType() {
		return returnType;
	}

	public MethodNameBuilder appendSql(String sql) {
		this.sql.getBuilder().append(sql);
		return this;
	}

	public Sqlbuilder getSqlbuilder() {
		return this.sql;
	}

	/**
	 * 获取指令并且当前指令指标下移动
	 * 
	 * @return
	 */
	public String next() {
		return this.instructions.get(this.current++);
	}

	public MethodNameBuilder nextAndReturn() {
		this.current++;
		return this;
	}

	public boolean hasNext() {
		return this.instructions.size() > this.current;
	}

	/**
	 * 提前查看一下下一条指令
	 * 
	 * @return
	 */
	public String perviewNext() {
		return this.hasNext() ? this.instructions.get(this.current) : null;
	}

	/**
	 * 获取下一条 如果不满足条件则返回默认值当默认值为空的时候抛出异常
	 * 
	 * @param isInstruction 是否为指令
	 * @param defaultValue
	 * @return
	 */
	public String next(boolean isInstruction, String defaultValue) {
		String next = perviewNext();
		if (InstructionManager.hasInstruction(next) == isInstruction) {
			return next();
		}
		if (defaultValue != null) {
			return defaultValue;
		}
		throw new ProxyException("需要的字段为指令 ? " + isInstruction + " 实际:" + next);
	}

	/**
	 * 查看是否存在这个参数
	 * 
	 * @param parameterKey
	 * @return
	 */
	public boolean concatParameter(String parameterKey) {
		return this.parameterMap.containsKey(parameterKey);
	}

	/**
	 * 获取参数
	 * 
	 * @param parameterKey
	 * @return
	 */
	public Parameter getParameter(String parameterKey) {
		ParameterModel parameterModel = this.parameterMap.get(parameterKey);
		if (parameterModel == null) {
			throw new ProxyException("parameter is null" + parameterKey);
		}
		return parameterModel.parameter;
	}

	public ParameterModel getParameterModel(Object parameterKey) {
		ParameterModel parameterModel = this.parameterMap.get(parameterKey);
		if (parameterModel == null) {
			throw new ProxyException("parameter is null " + parameterKey);
		}
		return parameterModel;
	}

	/**
	 * 获取参数
	 * 
	 * @param parameterKey
	 * @return
	 */
	public int getParameterIndex(String parameterKey) {

		return getParameterModel(parameterKey).index;
	}

	/**
	 * 获取参数
	 * 
	 * @param parameterKey
	 * @return
	 */
	public Parameter getParameterByIndex(int index) {
		return getParameterModel(index).parameter;
	}

	/**
	 * 获取参数
	 * 
	 * @param parameterKey
	 * @return
	 */
	public String getParameterArgs(String parameterKey) {
		ParameterModel model = this.parameterMap.get(parameterKey);
		if (model != null) {
			return "?" + model.index;
		}

		throw new ProxyException("parameter " + parameterKey + " is not found");
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void appendBody(String body) {
		this.body += body;
	}

	class ParameterModel {
		int index;
		Parameter parameter;

		/**
		 * @param index
		 * @param parameter
		 */
		public ParameterModel(int index, Parameter parameter) {
			this.index = index;
			this.parameter = parameter;
		}

	}

	public String getColumn(String backField) {
		FieldConfig fc = beanConfig.getField(backField);
		if (fc == null) {
			if ("id".equals(backField)) {
				List<FieldConfig> ids = beanConfig.getIdField();
				if (ids.size() < 1) {
					return "";
				}
				return ids.get(0).getColumn();
			}
			throw new ProxyException("字段不存在配置" + backField);
		}

		return fc.getColumn();
	}
}
