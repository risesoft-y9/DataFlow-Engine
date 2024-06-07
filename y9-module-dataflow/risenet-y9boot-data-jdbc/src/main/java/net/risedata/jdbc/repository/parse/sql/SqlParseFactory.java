package net.risedata.jdbc.repository.parse.sql;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.risedata.jdbc.commons.exceptions.ParseException;
import net.risedata.jdbc.repository.model.ArgsBuilder;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.repository.parse.sql.handles.ArgsParseHandle;
import net.risedata.jdbc.repository.parse.sql.handles.ReplaceParseHandle;

/**
 * 将sql 解析为可执行的sql <br/>
 * 动态sql规则 #{?index} 则会替换到sql 上 <br/>
 * ?index 则会用于jdbc传参 #{?1} 不会用于jdbc传参只会用在sql上
 * 
 * @author libo 2021年7月12日
 */
public class SqlParseFactory {

	private static final List<SqlParseHandle> PARSE_HANDLES = new ArrayList<>();

	static {
		PARSE_HANDLES.add(new ArgsParseHandle());
		PARSE_HANDLES.add(new ReplaceParseHandle());
	}

	/**
	 * 解析sql
	 * 
	 * @param type      返回值
	 * @param m         方法
	 * @param args      参数builder
	 * @param sourceSql 源sql
	 * @return methodBody
	 */
	public static String parseSql(ReturnType type, Method m, ArgsBuilder args, String sourceSql) {
		List<String> instructions = toInstructions(sourceSql);
		StringBuilder methodBody = new StringBuilder();
		StringBuilder sql = new StringBuilder(sourceSql);
		int find = 0;
		for (String instruction : instructions) {
			for (SqlParseHandle handle : PARSE_HANDLES) {
				if (handle.isHandle(instruction)) {
					find = sql.indexOf(instruction);
					sql.replace(find, find + instruction.length(),
							handle.parse(m, type, args, instruction, methodBody));
					continue;
				}
			}
		}
		if (!args.isDynamic()) {
			methodBody.append("java.lang.String _sql = ").append('"').append(sql).append('"').append(";");
		} else {
			methodBody.insert(0, "java.lang.StringBuilder $sql = new java.lang.StringBuilder();");
			methodBody.append("$sql.append(").append('"').append(sql).append('"').append(");");
			methodBody.append("java.lang.String _sql = $sql.toString();");
		}
		return methodBody.toString();

	}

	/**
	 * 解析为替换指令集
	 * 
	 * @param sourceSql
	 * @return
	 */
	private static List<String> toInstructions(String sourceSql) {
		List<String> instructions = new ArrayList<String>();
		int start = 0, end = 0;
		int count = 0;
		int parsedCount = 0;
		while (start < sourceSql.length()) {
			if (sourceSql.charAt(start) == '?') {
				if ((start + 1) < sourceSql.length() && isNumber(sourceSql.charAt(start + 1))) {
					if (count > 0) {
						throw new ParseException("? ?index concurrence unidentifiable" + sourceSql);
					}
					end = start + 2;
					while (end < sourceSql.length()) {
						if (isNumber(sourceSql.charAt(end))) {
							end++;
						} else {
							break;
						}
					}
					if (end > sourceSql.length()) {
						throw new ParseException(sourceSql.substring(start, end) + " unidentifiable");
					}
					instructions.add(sourceSql.substring(start, end));
					parsedCount++;
					start = end + 1;
				} else {
					if (parsedCount > 0) {
						throw new ParseException("? ?index concurrence unidentifiable" + sourceSql);
					}
					count++;
					start++;
				}
			} else if (sourceSql.charAt(start) == '#' && sourceSql.charAt(start + 1) == '{') {
				end = start + 2;
				while (end < sourceSql.length()) {
					if (sourceSql.charAt(end) == '}') {
						break;
					} else {
						end++;
					}
				}
				if (end > sourceSql.length()) {
					throw new ParseException(sourceSql.substring(start, end) + " unidentifiable");
				}
				instructions.add(sourceSql.substring(start, end + 1));
				start = end + 2;
			} else {
				start++;
			}
		}

		return instructions;
	}

	private static boolean isNumber(char c) {
		return c > 47 && c < 58;
	}

}
