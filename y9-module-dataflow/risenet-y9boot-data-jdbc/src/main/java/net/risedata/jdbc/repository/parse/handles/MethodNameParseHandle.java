package net.risedata.jdbc.repository.parse.handles;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.factory.BeanConfigFactory;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.repository.parse.MethodParseHandle;
import net.risedata.jdbc.repository.parse.handles.method.InstructionManager;
import net.risedata.jdbc.repository.parse.handles.method.MethodNameBuilder;
import net.risedata.jdbc.repository.parse.handles.method.StartedInstructionHandle;
import net.risedata.jdbc.utils.FieldUtils;

public class MethodNameParseHandle implements MethodParseHandle {

	@Override
	public boolean isHandle(Method m) {

		return AnnotationUtils.findAnnotation(m, Search.class) == null
				&& AnnotationUtils.findAnnotation(m, Modify.class) == null;
	}

	@Override
	public String parse(Method m, ReturnType returnType, ClassBuild properties) {
		MethodNameBuilder methodNameBuilder = new MethodNameBuilder(
				createInstructions(m.getName(), FieldUtils.getFields(properties.getGenericityClass())),
				m.getParameters(), BeanConfigFactory.getInstance(properties.getGenericityClass()), m, returnType);
		InstructionManager.get(StartedInstructionHandle.class, methodNameBuilder.perviewNext(), true)
				.handle(methodNameBuilder, properties);
		return methodNameBuilder.getBody();
	}

	private static boolean findParameter(List<Field> fields, String key, int i, String name) {
		key = FieldUtils.captureName(key);
		for (Field field : fields) {

			if (field.getName().startsWith(key) && field.getName().length() != key.length()) {
				String sub = field.getName().substring(key.length());
				if (i + sub.length() < name.length()) {
					for (int j = 0; j < sub.length(); j++) {
						if (sub.charAt(j) != name.charAt(i + j)) {
							return false;
						}

					}
					return true;
				}
			}
		}
		return false;
	}

	private static List<String> createInstructions(String methodName, List<Field> fields) {
		List<String> instructions = new ArrayList<String>();
		int start = 0;
		char tmp;
		String t;
		for (int i = 0; i < methodName.length(); i++) {
			tmp = methodName.charAt(i);
			if (Character.isUpperCase(tmp) || tmp == '$' || i == methodName.length() - 1) {
				if (i == methodName.length() - 1) {
					i++;
				}
				t = methodName.substring(start, i);
				if (InstructionManager.hasInstruction(t)) {
					instructions.add(methodName.substring(start, i));
				} else {
					if (InstructionManager.startsWith(t) || findParameter(fields, t, i, methodName)) {
						continue;
					}
					instructions.add(FieldUtils.captureName(methodName.substring(start, i)));
				}
				start = i;
			}

		}

		return instructions;

	}

}
