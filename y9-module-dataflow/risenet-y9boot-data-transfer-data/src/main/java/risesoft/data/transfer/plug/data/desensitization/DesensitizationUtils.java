package risesoft.data.transfer.plug.data.desensitization;

/**
 * 脱敏工具类
 * 
 * @author lb
 * @date 2023年4月26日 下午5:22:50
 */
public class DesensitizationUtils {

	/**
	 * 对字符串进行脱敏替换
	 * 
	 * @param str             字符串
	 * @param percentage      脱敏留存百分比
	 * @param startPercentage 起始位置百分比
	 * @param symbol          占位符
	 * @return
	 */
	public static String desensitization(String str, double percentage, double startPercentage, String symbol) {
		if (str.equals("") || str == null) {
			return "";
		}
		int size = (int) (str.length() * percentage);
		int start = (int) (str.length() * startPercentage);
		if (start == 0 && startPercentage > 0.0) {
			start = 1;
		}
		String end = str.substring(start);
		StringBuilder stringBuilder = new StringBuilder(str.substring(0, start));
		for (int i = 0; i < end.length(); i++) {
			if (i < size) {
				stringBuilder.append(symbol);
			} else {
				stringBuilder.append(end.charAt(i));
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * 脱敏
	 * 
	 * @param str             需要被脱敏的字符串
	 * @param percentage      脱敏百分比
	 * @param startPercentage 脱敏开始百分比
	 * @return
	 */
	public static String desensitization(String str, double percentage, double startPercentage) {
		return desensitization(str, percentage, startPercentage, "*");
	}

	/**
	 * 脱敏 使用默认的百分比 和脱敏字符串
	 * 
	 * @param str 需要被脱敏的字符串
	 * @return
	 */
	public static String desensitization(String str) {
		return desensitization(str, 0.5, 0.4, "*");
	}

	
}
