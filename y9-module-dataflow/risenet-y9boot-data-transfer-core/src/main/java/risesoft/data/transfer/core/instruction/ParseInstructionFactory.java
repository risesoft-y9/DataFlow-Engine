package risesoft.data.transfer.core.instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import risesoft.data.transfer.core.config.ConfigLoad;
import risesoft.data.transfer.core.config.ConfigLoadManager;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.InstallException;
import risesoft.data.transfer.core.instruction.factory.InstructionFactory;
import risesoft.data.transfer.core.start.StartConfiguration;
import risesoft.data.transfer.core.util.ClassTools;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 解析 函数指令，用于执行任务前对配置文件中的函数进行解析并执行
 * 
 * 
 * @typeName ParseFunctionInstructionFactory
 * @date 2024年8月23日
 * @author lb
 */
public class ParseInstructionFactory implements StartConfiguration {

	/**
	 * 指令工厂存储
	 */
	private static final Map<String, InstructionFactory> INSTRUCTION_FACTORY_MAP = new HashMap<String, InstructionFactory>();

	static {
		ConfigLoadManager.addLoad(new ConfigLoad() {

			@Override
			public Configuration laod(Configuration config, JobContext jobContext) {
				String jsonConfig = config.toJSON();
				List<MatchModel> matchs = parseNestedPatterns(jsonConfig, 0);
				jsonConfig = doInstruction(parseNestedPatterns(jsonConfig, 0), jsonConfig, jsonConfig, jobContext);
				return matchs.size() == 0 ? config : Configuration.from(jsonConfig);
			}

			public String doInstruction(List<MatchModel> matchs, String input, String jsonConfig,
					JobContext jobContext) {
				String newSource;
				for (MatchModel match : matchs) {
					newSource = doInstruction(parseNestedPatterns(match.getSource(), 1), match.getArgs(), jsonConfig,
							jobContext);
					InstructionFactory instructionFactory = INSTRUCTION_FACTORY_MAP.get(match.getMethodName());
					if (instructionFactory != null) {
						input = input.replace(match.getSource(), instructionFactory
								.getInstance(newSource.split("#"), jsonConfig).executor(jsonConfig, jobContext));
					}
				}
				return input;
			}

		});
	}

	static class MatchModel {
		private String source;
		private String methodName;
		private String args;

		public String getSource() {
			return source;
		}

		public String getMethodName() {
			return methodName;
		}

		public String getArgs() {
			return args;
		}

	}

	public static List<MatchModel> parseNestedPatterns(String input, int startIndex) {
		List<MatchModel> matches = new ArrayList<>();
		int length = input.length();
		int i = startIndex;
		MatchModel matchModel;
		while (i < length) {
			if (input.charAt(i) == '$') {
				int start = i;
				i++;
				while (i < length && Character.isLetterOrDigit(input.charAt(i))) {
					i++;
				}

				// 假如在其中又找到了
				if (i < length && input.charAt(i) == '{') {
					i++; // Skip the '{'
					int nestedStart = i;
					int nestedDepth = 1;
					matchModel = new MatchModel();
					matchModel.methodName = input.substring(start + 1, i - 1);
					// 在{ 中继续找} 避免存在多层嵌套
					while (i < length && nestedDepth > 0) {
						if (input.charAt(i) == '{') {
							nestedDepth++;

						} else if (input.charAt(i) == '}') {
							nestedDepth--;
						}
						i++;
					}

					if (nestedDepth == 0) {
						String match = input.substring(start, i);
						matchModel.source = match;
						matchModel.args = input.substring(nestedStart, i - 1);
						matches.add(matchModel);
						while (i < length && Character.isWhitespace(input.charAt(i))) {
							i++;
						}
					} else {
						// 有开口没有关闭
						throw new IllegalArgumentException("Unmatched '{' in nested pattern at index " + i);
					}
				} else {
					i = start + 1;
				}
			} else {
				i++;
			}
		}

		return matches;
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
