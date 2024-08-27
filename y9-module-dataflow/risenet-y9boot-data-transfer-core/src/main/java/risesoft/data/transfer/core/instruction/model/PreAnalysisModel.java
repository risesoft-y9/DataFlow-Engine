package risesoft.data.transfer.core.instruction.model;

import java.util.Arrays;

/**
 * 预解析的model
 * 
 * 
 * @typeName PreanalysisModel
 * @date 2024年8月23日
 * @author lb
 */
public class PreAnalysisModel {

	/**
	 * 源字符串
	 */
	private String source;
	/**
	 * 方法
	 */
	private String method;
	/**
	 * 参数
	 */
	private String[] args;

	public PreAnalysisModel(String source, String method, String[] args) {
		super();
		this.source = source;
		this.method = method;
		this.args = args;
	}

	/**
	 * 获取完整的指令信息
	 * 
	 * @return
	 */
	public String getSource() {
		return source;
	}

	/**
	 * 获取方法信息
	 * 
	 * @return
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * 获取参数
	 * 
	 * @return
	 */
	public String[] getArgs() {
		return args;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(args);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PreAnalysisModel other = (PreAnalysisModel) obj;
		return Arrays.equals(args, other.args);
	}

}
