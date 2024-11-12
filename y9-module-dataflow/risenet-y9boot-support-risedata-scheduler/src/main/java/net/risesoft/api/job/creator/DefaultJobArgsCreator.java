package net.risesoft.api.job.creator;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.JobContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 字符串创建类 #{方法名:参数1%%参数2}
 * 
 * @author lb
 *
 */
@Component
public class DefaultJobArgsCreator implements JobArgsCreator {
	private static Pattern p = Pattern.compile("\\#\\{(.*?)\\}");

	@Autowired
	private Map<String, CreatorMethod> creatorMethods;

	@Override
	public String creator(JobContext context, String creatorStr) {

		Matcher match = p.matcher(creatorStr);
		String group = null;
		String[] value;
		String[] args;

		CreatorMethod creatorMethod;
		while (match.find()) {
			group = match.group();
			value = match.group(1).split("^");
			args = null;
			creatorMethod = creatorMethods.get(value[0]);
			if (creatorMethod == null) {
				throw new JobException("方法不存在" + value[0]);
			}
			if (value.length > 1) {
				args = value[1].split("%%");
			}
			creatorStr = creatorStr.replace(group, creatorMethod.create(context, args));
		}
		return creatorStr;
	}

}
