package risesoft.data.transfer.plug.data.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class DateUtils {
	private static final Map<String, ThreadLocal<SimpleDateFormat>> FORMATTERS = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

	private static final Map<String, Pattern> PATTERNS = new HashMap<>();

	private static final Map<String, Format<List<String>>> PATTERN_FORMATS = new HashMap<String, Format<List<String>>>();

	private static final Map<String, String> FORMAT_MAP = new HashMap<>();

	private static final Pattern FORMAT_PATTERN = Pattern.compile("[YyMmDdSsHhWwEFZz]{1,4}");

	private static Object LOCK = new Object();

	private static Object PATTERN_LOCK = new Object();

	public static String format(Date date, String pattern) {
		return getFormat(pattern).format(date);
	}

	static {
		addDefaultFormat();
	}

	public static SimpleDateFormat getFormat(String pattern) {
		ThreadLocal<SimpleDateFormat> df = FORMATTERS.get(pattern);
		if (df == null) {
			synchronized (LOCK) {
				df = FORMATTERS.get(pattern);
				if (df == null) {
					df = new ThreadLocal<SimpleDateFormat>() {
						@Override
						protected SimpleDateFormat initialValue() {
							return new SimpleDateFormat(pattern);
						}
					};
					FORMATTERS.put(pattern, df);
				}
			}
		}
		return df.get();
	}

	public static Date parse(String text, String pattern) throws ParseException {
		return getFormat(pattern).parse(text);
	}

	/**
	 * 将一个字符串解析为时间 此字符串格式必须是先注册到缓存中的格式
	 * 
	 * @param source
	 * @return
	 */
	public static Date parse(String source) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		Set<String> keys = PATTERN_FORMATS.keySet();
		Format<List<String>> format;
		int differ = 0;
		for (String key : keys) {
			format = PATTERN_FORMATS.get(key);
			differ = format.getLength() - source.length();
			if (differ < 3 && differ > -3) {
				Pattern p = PATTERNS.get(format.getFormat());
				Matcher math = p.matcher(source);
				if (math.find()) {
					List<String> list = format.getPattern();
					for (String formater : list) {
						try {
							return parse(source, formater);
						} catch (ParseException e) {
						}
					}
				}
			}
		}
		throw new RuntimeException(source + " cannot parse");
	}

	public static void addFormat(String format) {
		getPattern(format);
	}

	public static void addDefaultFormat() {
		addFormat("yyyy年MM月dd日 HH时mm分ss秒");
		addFormat("yyyy/MM/dd HH:mm");
		addFormat("yyyy/MM/d");
		addFormat("yyyy/M/d HH:mm");
		addFormat("yyyy-MM-dd HH:mm:ss");
		addFormat("yyyy年MM月dd日");
		addFormat("yyyy-MM-dd");
		addFormat("yyyy.MM");
		addFormat("yyyy.M.dd");
		addFormat("yyyy.MM.d");
		addFormat("yyyy.MM.dd");
		addFormat("yyyy年MM月");
	}

	/**
	 * 将常见的表达式装换为 正则表达式的匹配并保存到缓存
	 * 
	 * @param format
	 * @return 解析出来的正则表达式
	 */
	public static String getPattern(String format) {
		String pattern = FORMAT_MAP.get(format);
		if (pattern != null) {
			return pattern;
		}
		Matcher m = FORMAT_PATTERN.matcher(format);
		String temp = null;
		StringBuilder newexpression = new StringBuilder(format);
		int differ = 0;
		String newtemp = null;
		while (m.find()) {
			temp = m.group();
			newtemp = "\\d{1," + temp.length() + "}";
			newexpression = newexpression.replace(m.start() + differ, m.end() + differ, newtemp);
			differ += newtemp.length() - temp.length();
		}
		String patternStr = "(" + newexpression.toString() + ")";
		Format<List<String>> formats = PATTERN_FORMATS.get(patternStr);
		if (formats == null) {
			synchronized (PATTERN_LOCK) {
				formats = PATTERN_FORMATS.get(patternStr);
				if (formats == null) {
					formats = new Format<List<String>>();
					formats.setFormat(patternStr);
					formats.setLength(format.length());
					PATTERN_FORMATS.put(patternStr, formats);
					if (!PATTERNS.containsKey(patternStr)) {
						PATTERNS.put(patternStr, Pattern.compile(patternStr));
					}
				}
			}
		}
		synchronized (formats) {
			List<String> formatchilds = formats.getPattern();
			if (formatchilds == null) {
				formatchilds = new ArrayList<>();
				formats.setPattern(formatchilds);
			}
			formatchilds.add(format);
		}
		return patternStr;
	}

	/**
	 * 将时间增加
	 * 
	 * @param date 源时间
	 * @param day  增加的天数
	 * @return 返回 增加后的时间
	 */
	public static Date updateDay(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return calendar.getTime();
	}
}

class Format<T> {
	private T pattern;
	private String format;
	private int length;

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the pattern
	 */
	public T getPattern() {
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(T pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	public String toString() {
		return "Format [pattern=" + pattern + ", format=" + format + "]";
	}

}
