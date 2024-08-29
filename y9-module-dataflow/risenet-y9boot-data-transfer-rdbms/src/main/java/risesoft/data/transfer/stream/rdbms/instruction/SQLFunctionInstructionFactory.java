package risesoft.data.transfer.stream.rdbms.instruction;

import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.instruction.ParseInstructionFactory;
import risesoft.data.transfer.core.instruction.factory.Instruction;
import risesoft.data.transfer.core.instruction.factory.InstructionFactory;
import risesoft.data.transfer.core.start.StartConfiguration;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ConfigurationConst;
import risesoft.data.transfer.core.util.ValueUtils;

/**
 * 执行sql方法的指令构建工厂
 * 
 * 
 * @typeName SQLFunctionInstructionFactory
 * @date 2024年8月27日
 * @author lb
 */
public class SQLFunctionInstructionFactory implements InstructionFactory ,StartConfiguration{
	static {
		ParseInstructionFactory.registerFactory(new SQLFunctionInstructionFactory());
	}

	@Override
	public String getName() {
		return "sql";
	}

	/**
	 * 获取实例 args: 0: 函数名 1: 目标数据库 in为输入流 out为输出流 2：where 2：目标数据库索引为配置文件的索引 默认为第0个
	 * sql 效果 select args[0] from args[1]args[3].table where args[2]
	 */
	@Override
	public Instruction getInstance(String[] args, String config) {
		if (args.length < 2) {
			throw TransferException.as(CommonErrorCode.CONFIG_ERROR, "缺少配置量!");
		}
		String functionName = ValueUtils.getRequired(args[0], "缺失必须的函数!");
		String position = ValueUtils.getRequired(args[1], "缺失必须的定位!");
		Integer index = args.length == 4 ? Integer.parseInt(args[3]) : 0;
		Configuration configuration = Configuration.from(config);
		configuration = configuration.getListConfiguration(ConfigurationConst.JOBS).get(index)
				.getConfiguration(position).getConfiguration("args");
		String sql = "select " + functionName + " from " + configuration.getString("tableName");
		if (args.length >= 3 && StringUtils.isNotEmpty(args[2])) {
			sql += "where " + args[2];
		}
		return new SQLFunctionInstruction(sql, configuration.getString("jdbcUrl"), configuration.getString("userName"),
				configuration.getString("password"));
	}

}
