package risesoft.data.transfer.core.instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import risesoft.data.transfer.core.config.ConfigLoad;
import risesoft.data.transfer.core.config.ConfigLoadManager;
import risesoft.data.transfer.core.exception.InstallException;
import risesoft.data.transfer.core.instruction.factory.InstructionFactory;
import risesoft.data.transfer.core.util.ClassTools;
import risesoft.data.transfer.core.util.ClassUtils;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 解析 函数指令，用于执行任务前对配置文件中的函数进行解析并执行
 * 
 * 
 * @typeName ParseFunctionInstructionFactory
 * @date 2024年8月23日
 * @author lb
 */
public class ParseInstructionFactory {
	private static final String rgex = "\\$(.*?)\\{(.*?)\\}";
	private static final Pattern p = Pattern.compile(rgex);

	/**
	 * 指令工厂存储
	 */
	private static final Map<String, InstructionFactory> INSTRUCTION_FACTORY_MAP = new HashMap<String, InstructionFactory>();

	static {
		ConfigLoadManager.addLoad(new ConfigLoad() {

			@Override
			public Configuration laod(Configuration config) {
				String jsonConfig = config.toJSON();
				Matcher matcher = p.matcher(jsonConfig);
				Map<String, Byte> setMap = new HashMap<String, Byte>();
				while (matcher.find()) {
					if (setMap.containsKey(matcher.group(0))) {
						continue;
					}
					setMap.put(matcher.group(0), Byte.MIN_VALUE);
					InstructionFactory instructionFactory = INSTRUCTION_FACTORY_MAP.get(matcher.group(1));
					if (instructionFactory != null) {
						jsonConfig = jsonConfig.replace(matcher.group(0),
								instructionFactory.getInstance(matcher.group(2).split(",")).executor(jsonConfig));
					}
				}
				return setMap.size() == 0 ? config : Configuration.from(jsonConfig);
			}
		});
	}

	/**
	 * 根据指令获取指令工厂
	 * 
	 * @param instruction
	 * @return
	 */
	public static InstructionFactory getInstance(String instruction) {
		return INSTRUCTION_FACTORY_MAP.get(instruction);
	}

	/**
	 * 将指令工厂注册
	 * 
	 * @param instructionFactory
	 */
	public static void registerFactory(InstructionFactory instructionFactory) {
		INSTRUCTION_FACTORY_MAP.put(instructionFactory.getName(), instructionFactory);
	}

	/**
	 * 从包中注册
	 * 
	 * @param packageName
	 */
	public static void registerOfPackage(String packageName) {

		try {
			List<InstructionFactory> instructionFactorys = ClassTools.getInstancesOfPack(packageName,
					InstructionFactory.class);
			for (InstructionFactory instructionFactory : instructionFactorys) {
				registerFactory(instructionFactory);
			}
		} catch (Exception e) {
			throw new InstallException("从包" + packageName + "中注册指令工厂失败!" + e.getMessage());
		}

	}
}
