package risesoft.data.transfer.core.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteUtils {

	/**
	 * 将字节（bytes）转换为可读的字符串（如 KB、MB、GB、TB）
	 * 
	 * @param bytes 字节大小
	 * @return 格式化后的字符串（例如："1.5 GB"）
	 */
	public static String formatBytes(long bytes) {
		if (bytes < 0) {
			throw new IllegalArgumentException("字节大小不能为负数");
		}

		// 定义单位（B、KB、MB、GB、TB）
		final String[] units = { "B", "KB", "MB", "GB", "TB" };
		int unitIndex = 0;
		double size = bytes;

		// 计算合适的单位（每次除以 1024，直到 size < 1024）
		while (size >= 1024 && unitIndex < units.length - 1) {
			size /= 1024;
			unitIndex++;
		}

		// 保留两位小数（可选）
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(size) + " " + units[unitIndex];
	}

	static Pattern pattern = Pattern.compile("^(\\d+\\.?\\d*)\\s*([KMGTP]?B?)$", Pattern.CASE_INSENSITIVE);

	/**
	 * 将带有单位的字符串（如 "10KB", "5.5MB", "2GB"）转换为字节大小
	 * 
	 * @param sizeStr 带有单位的字符串（支持 KB、MB、GB、TB，不区分大小写）
	 * @return 对应的字节大小
	 * @throws IllegalArgumentException 如果格式无效或数值溢出
	 */
	public static long convertToBytes(String sizeStr) {
		if (sizeStr == "" || sizeStr.trim().isEmpty()) {
			throw new IllegalArgumentException("输入字符串不能为空");
		}

		// 正则匹配数字和单位（支持小数，如 "1.5GB"）

		Matcher matcher = pattern.matcher(sizeStr.trim());

		if (!matcher.find()) {
			throw new IllegalArgumentException("无效的格式，示例: '10KB', '5.5MB', '2 GB'");
		}

		// 提取数值部分
		double value = Double.parseDouble(matcher.group(1));
		String unit = matcher.group(2).toUpperCase();

		// 根据单位计算字节大小
		long bytes;
		switch (unit) {
		case "B":
			bytes = (long) value;
			break;
		case "KB":
			bytes = (long) (value * 1024);
			break;
		case "MB":
			bytes = (long) (value * 1024 * 1024);
			break;
		case "GB":
			bytes = (long) (value * 1024 * 1024 * 1024);
			break;
		case "TB":
			bytes = (long) (value * 1024 * 1024 * 1024 * 1024);
			break;
		case "PB":
			bytes = (long) (value * 1024 * 1024 * 1024 * 1024 * 1024);
			break;
		default:
			throw new IllegalArgumentException("不支持的单位: " + unit);
		}

		// 检查溢出
		if (bytes < 0) {
			throw new IllegalArgumentException("数值溢出，结果为负数");
		}

		return bytes;
	}

	public static void main(String[] args) {
		// 测试
		System.out.println(formatBytes(500)); // 500 B
		System.out.println(formatBytes(2048)); // 2 KB
		System.out.println(formatBytes(1_048_576)); // 1 MB
		System.out.println(formatBytes(1_073_741_824)); // 1 GB
		System.out.println(formatBytes(5_000_000_000L)); // 4.66 GB
	}
}