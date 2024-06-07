package net.risesoft.api.utils.job;
//package net.risesoft.api.utils.job;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.script.Invocable;
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//
//
///**
// * js语言操作器
// *
// * @author lb
// *
// */
//
//public class ScriptManager {
//
//	private static ScriptEngineManager manager = new ScriptEngineManager();
//	private static ScriptEngine engine = manager.getEngineByName("js");
//
//	private static Map<String, String> invocablesMap = new HashMap<String, String>();
//
//	/**
//	 * 执行单个
//	 *
//	 * @param <T>
//	 * @param code 代码
//	 * @param item 参数
//	 * @param cla  class
//	 * @return
//	 * @throws Exception
//	 */
//	public static <T> T executorReturnItem(String code, @SuppressWarnings("rawtypes") Map item,
//			Map<String, String> args, Class<T> cla) throws Exception {
////TODO $管理器
//		String invocableMethodName = getInvocableMethodName(
//				"(value,args,utils){ " + (code.indexOf("return ") == -1 ? "return " : "") + code + "}", code);
//		Invocable invoke = (Invocable) engine;
//		return cla.cast(invoke.invokeFunction(invocableMethodName, item, args, scriptUtils));
//	}
//	/**
//	 * 执行全部
//	 *
//	 * @param <T>
//	 * @param code 代码
//	 * @param item 参数
//	 * @param cla  class
//	 * @return
//	 * @throws Exception
//	 */
//	public static <T> T executorReturn(String code, @SuppressWarnings("rawtypes") Map<String,DataSet> items,
//			Map<String, String> args, Class<T> cla) throws Exception {
//
//		String invocableMethodName = getInvocableMethodName(
//				"(context,args,l){ " + (code.indexOf("return ") == -1 ? "return " : "") + code + "}", code);
//		Invocable invoke = (Invocable) engine;
//		return cla.cast(invoke.invokeFunction(invocableMethodName, items, args, scriptUtils));
//	}
//	private static String getInvocableMethodName(String code, String key) throws Exception {
//		String invocableMethodName = invocablesMap.get(key);
//		if (invocableMethodName == null) {
//			invocableMethodName = "$" + System.currentTimeMillis();
//			invocablesMap.put(key, invocableMethodName);
//			engine.eval("function " + invocableMethodName + code);
//		}
//		return invocableMethodName;
//	}
//}
