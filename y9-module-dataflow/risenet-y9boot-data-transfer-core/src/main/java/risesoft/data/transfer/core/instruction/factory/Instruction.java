package risesoft.data.transfer.core.instruction.factory;

import risesoft.data.transfer.core.util.Configuration;

/**
 * 指令
 * 
 * @typeName Instruction
 * @date 2024年8月23日
 * @author lb
 */
public interface Instruction {
	/**
	 * 执行指令
	 * 
	 * @param config 配置文件的字符串形式
	 * @return
	 */
	String executor(String config);
}
