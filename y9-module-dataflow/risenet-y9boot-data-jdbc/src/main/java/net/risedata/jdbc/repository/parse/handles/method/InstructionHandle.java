package net.risedata.jdbc.repository.parse.handles.method;

import net.risedata.jdbc.repository.model.ClassBuild;

/**
 * 解析指令的执行器
 * 
 * @author lb
 * @date 2023年3月13日 上午11:41:15
 */
public interface InstructionHandle {

	/**
	 * 此 解析指令执行器执行的指令
	 * 
	 * @return
	 */
	String handleInstruction();

	/**
	 * 执行handle
	 * 
	 * @param builder
	 */
	void handle(MethodNameBuilder builder,ClassBuild classBuild);

}
