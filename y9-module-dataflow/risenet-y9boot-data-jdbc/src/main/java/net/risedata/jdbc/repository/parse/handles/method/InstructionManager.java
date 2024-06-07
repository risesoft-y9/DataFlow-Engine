package net.risedata.jdbc.repository.parse.handles.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.risedata.jdbc.exception.ProxyException;
import net.risedata.jdbc.repository.parse.handles.method.order.OrderInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.section.AllInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.section.DistinctInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.section.FieldInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.started.DeleteInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.started.SearchInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.started.UpdateInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.AndInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.ByInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.condition.BetweenInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.condition.ContainingInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.condition.EndingWithInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.condition.FalseInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.condition.IgnoreCaseInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.condition.SimpledInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.condition.StartingWithInstructionHandle;
import net.risedata.jdbc.repository.parse.handles.method.where.condition.TrueInstructionHandle;

/**
 * 指令集管理器
 * 
 * @author lb
 * @date 2023年3月13日 下午3:01:33
 */
public class InstructionManager {

	private static final Map<Class<? extends InstructionHandle>, Map<String, InstructionHandle>> INSTRUCTION_CLASS_MAP = new HashMap<Class<? extends InstructionHandle>, Map<String, InstructionHandle>>();

	private static final Map<String, InstructionHandle> INSTRUCTION_MAP = new HashMap<>();
	static {
		//TODO 此处可以动态加载通过load器
		initInstruction(new FieldInstructionHandle());
		initInstruction(new DistinctInstructionHandle());
		initInstruction(new SearchInstructionHandle());
		initInstruction(new DeleteInstructionHandle());
		initInstruction(new UpdateInstructionHandle());
		initInstruction(new BetweenInstructionHandle());
		initInstruction(new SimpledInstructionHandle());
		initInstruction(new ByInstructionHandle());
		initInstruction(new OrderInstructionHandle());
		initInstruction(new AndInstructionHandle());
		initInstruction(new OrderInstructionHandle());
		initInstruction(new StartingWithInstructionHandle());
		initInstruction(new EndingWithInstructionHandle());
		initInstruction(new TrueInstructionHandle());
		initInstruction(new FalseInstructionHandle());
		initInstruction(new IgnoreCaseInstructionHandle());
		initInstruction(new ContainingInstructionHandle());
		initInstruction(new AllInstructionHandle());
	}

	@SuppressWarnings("unchecked")
	public static void initInstruction(InstructionHandle instructionHandle) {
		String[] instructions = instructionHandle.handleInstruction().split(",");
		putAll(instructions, INSTRUCTION_MAP, instructionHandle);
		List<Class<?>> classes = getInterfaces(instructionHandle.getClass());
		for (Class<?> cla : classes) {
			if (InstructionHandle.class.isAssignableFrom(cla)) {
				Map<String, InstructionHandle> instructionMap = INSTRUCTION_CLASS_MAP.get(cla);
				if (instructionMap == null) {
					instructionMap = new HashMap<>();
					INSTRUCTION_CLASS_MAP.put((Class<? extends InstructionHandle>) cla, instructionMap);
				}
				INSTRUCTION_CLASS_MAP.put((Class<? extends InstructionHandle>) cla, instructionMap);
				putAll(instructions, instructionMap, instructionHandle);
			}
		}

	}

	private static void putAll(String[] instructions, Map<String, InstructionHandle> map,
			InstructionHandle instructionHandle) {
		for (int i = 0; i < instructions.length; i++) {
			map.put(instructions[i], instructionHandle);
		}
	}

	private static List<Class<?>> getInterfaces(Class<?> cla) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		loadInterfaces(cla, classes);
		return classes;
	}

	private static void loadInterfaces(Class<?> cla, List<Class<?>> classes) {
		if (cla != Object.class) {
			Class<?>[] classe = cla.getInterfaces();
			for (Class<?> class1 : classe) {
				classes.add(class1);
			}
			loadInterfaces(cla.getSuperclass(), classes);
		}
	}

	public static Map<String, InstructionHandle> get(Class<? extends InstructionHandle> instructionClass) {
		return INSTRUCTION_CLASS_MAP.get(instructionClass);
	}

	public static InstructionHandle get(Class<? extends InstructionHandle> instructionClass, String key,
			boolean required) {
		InstructionHandle instructionHandle = INSTRUCTION_CLASS_MAP.get(instructionClass).get(key);
		if (required && instructionHandle == null) {
			throw new ProxyException("未识别的指令:" + key);
		}
		return instructionHandle;
	}

	public static boolean hasInstruction(String key) {
		return INSTRUCTION_MAP.containsKey(key);
	}

	public static boolean hasInstruction(Class<? extends InstructionHandle> instructionClass, String key) {
		return INSTRUCTION_CLASS_MAP.get(instructionClass).containsKey(key);
	}

	public static InstructionHandle get(String key) {
		return INSTRUCTION_MAP.get(key);
	}

	public static boolean startsWith(String name) {
		Set<String> keys = INSTRUCTION_MAP.keySet();
		for (String key : keys) {
			if (key.startsWith(name)) {
				return true;
			}
		}
		return false;
	}

}
