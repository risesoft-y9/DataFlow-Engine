package risesoft.data.transfer.stream.rdbms.instruction;

import java.util.Arrays;


import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.instruction.ParseInstructionFactory;
import risesoft.data.transfer.core.instruction.factory.Instruction;
import risesoft.data.transfer.core.instruction.factory.InstructionFactory;
import risesoft.data.transfer.core.start.StartConfiguration;

/**
 * 判断是否存在sql =''的情况如果有则替换成其他字符串
 * 
 * 
 * @typeName IfSqlNullFunctionInstructionFactory
 * @date 2025年1月16日
 * @author lb
 */
public class IfSqlNullFunctionInstructionFactory implements InstructionFactory, StartConfiguration {
	static {
		ParseInstructionFactory.registerFactory(new IfSqlNullFunctionInstructionFactory());
	}

	@Override
	public String getName() {
		return "sqlNull";
	}

	/**
	 *接受参数 args[0] 判断的sql args[1] 替换值 默认为空
	 */
	@Override
	public Instruction getInstance(String[] args, String config) {
		System.out.println(Arrays.asList(args));
		if (args.length < 1) {
			throw TransferException.as(CommonErrorCode.CONFIG_ERROR, "缺少配置量!");
		}
		
		
		return new Instruction() {
			
			@Override
			public String executor(String config, JobContext jobContext) {
				if (args[0].indexOf("''")!=-1) {
					return args.length==2?args[1]:"";
				}
				return args[0];
			}
		};
	}

}
