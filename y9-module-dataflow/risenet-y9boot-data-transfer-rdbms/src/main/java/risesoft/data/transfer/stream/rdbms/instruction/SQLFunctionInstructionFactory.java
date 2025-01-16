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
import risesoft.data.transfer.stream.rdbms.utils.DataBaseType;

/**
 * 执行sql方法的指令构建工厂 执行的是查询方法
 * 
 * @typeName SQLFunctionInstructionFactory
 * @date 2024年8月27日
 * @author lb
 */
public class SQLFunctionInstructionFactory implements InstructionFactory, StartConfiguration {
	static {
		ParseInstructionFactory.registerFactory(new SQLFunctionInstructionFactory());
	}

	@Override
	public String getName() {
		return "sql";
	}

	/**
	 * 获取实例 args: 0: 函数名 1: 目标数据库 input为输入流 output为输出流 2：where 3：目标数据库索引为配置文件的索引
	 * 默认为第0个
	 * 4在哪个定位的数据库类型上执行，默认是相反的数据库上执行如选择了input则在output上执行返回的数据会被处理为output数据库可识别的数据 sql
	 * 效果 select args[0] from args[1]args[3].table where args[2]
	 */
	@Override
	public Instruction getInstance(String[] args, String config) {
		if (args.length < 2) {
			throw TransferException.as(CommonErrorCode.CONFIG_ERROR, "缺少配置量!");
		}
		String functionName = ValueUtils.getRequired(args[0], "缺失必须的函数!");
		String position = ValueUtils.getRequired(args[1], "缺失必须的定位!");
		Integer index = args.length >= 4 ? Integer.parseInt(args[3]) : 0;
		Configuration configurationFrom = Configuration.from(config);
		Configuration configuration = configurationFrom.getListConfiguration(ConfigurationConst.JOBS).get(index)
				.getConfiguration(position).getConfiguration("args");
		String sql = "select " + functionName + " from " + configuration.getString("tableName");
		if (args.length >= 3 && StringUtils.isNotEmpty(args[2])) {
			sql += "where " + args[2];
		}
		String dataPosition;
		if (args.length >= 5) {
			dataPosition = args[4];
		} else {
			dataPosition = position.equals("output") ? "input" : "output";
		}
		return new SQLFunctionInstruction(sql, configuration.getString("jdbcUrl"), configuration.getString("userName"),
				configuration.getString("password"),
				DataBaseType.getDataBaseTypeByJdbcUrl(configuration.getString("jdbcUrl")),
				DataBaseType.getDataBaseTypeByJdbcUrl(configurationFrom.getListConfiguration(ConfigurationConst.JOBS)
						.get(index).getConfiguration(dataPosition).getConfiguration("args").getString("jdbcUrl")));
	}

}
