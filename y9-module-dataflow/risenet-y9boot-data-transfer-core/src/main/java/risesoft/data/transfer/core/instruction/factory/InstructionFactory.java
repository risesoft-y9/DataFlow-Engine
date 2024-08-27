package risesoft.data.transfer.core.instruction.factory;

/**
 * 指令工厂用于处理某个特定指令的产生
 * 
 * 
 * @typeName InstructionFactory
 * @date 2024年8月23日
 * @author lb
 */
public interface InstructionFactory {

	/**
	 * 获取工厂名字用于函数名
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 获取指令
	 * 
	 * @param args
	 * @return
	 */
	Instruction getInstance(String[] args,String config);
}
