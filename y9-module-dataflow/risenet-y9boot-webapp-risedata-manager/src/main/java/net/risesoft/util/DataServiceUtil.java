package net.risesoft.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务配置工具类
 * @author pzx
 *
 */
public class DataServiceUtil {
	
	/**
	 * 数据闸口
	 */
	public static final String EXCHANGE = "exchange";
	
	/**
	 * 输入、输出线程池
	 */
	public static final String EXECUTOR = "executor";
	
	/**
	 * 输入、输出通道
	 */
	public static final String CHANNEL = "channel";
	
	/**
	 * 其它插件
	 */
	public static final String PLUGS = "plugs";
	
	/**
	 * 日志打印
	 */
	public static final String PRINTLOG = "printLog";
	
	/**
	 * 脏数据处理
	 */
	public static final String DIRTYDATA = "dirtyData";
	
	/**
	 * 数据脱敏
	 */
	public static final String MASK = "mask";
	
	/**
	 * 数据加密
	 */
	public static final String ENCRYP = "encryp";
	
	/**
	 * 异字段
	 */
	public static final String DIFFERENT = "different";
	/**
	 * 异字段执行类
	 */
	public static final String DIFFERENTCLASS = "risesoft.data.transfer.plug.data.rename.FieldReNamePlug";
	
	/**
	 * 时间格式
	 */
	public static final String DATE = "date";
	
	/**
	 * 数据转换
	 */
	public static final String CONVERT = "convert";
	
	/**
	 * 输出
	 */
	public static final String OUTPUT = "output";
	
	/**
	 * 输入
	 */
	public static final String INPUT = "input";

	public static String getTitle(String name) {
		switch (name) {
			case "output":
				return "输出";
			case "input":
				return "输入";
			case "dirtyData":
				return "脏数据处理";
			case "printLog":
				return "日志";
			case "sql1":
				return "前置SQL";
			case "sql2":
				return "后置SQL";
			case "sql3":
				return "后置成功SQL";
			case "sql4":
				return "后置失败SQL";
			default:
				return name;
		}
	}
	
	/**
	 * 根据某个key值对listmap分类
	 * @param <K>
	 * @param <V>
	 * @param maps
	 * @param classifyKey
	 * @return
	 */
    public static <K, V> Map<V, List<Map<K, V>>> classifyMaps(List<Map<K, V>> maps, String keyName) {  
        Map<V, List<Map<K, V>>> classified = new HashMap<>();  
        for (Map<K, V> map : maps) {  
            V category = map.get(keyName);
            if (category != null) {  
                classified.computeIfAbsent(category, k -> new ArrayList<>()).add(map);  
            }  
        }  
        return classified;  
    }
    
    /**
     * 根据参数获取编码方式
     * @param num
     * @return
     */
    public static String getEncoding(int num) {
		switch (num) {
			case 1:
				return "ISO-8859-1";
			case 2:
				return "GBK";
			case 3:
				return "UTF-8";
			case 4:
				return "ASCII";
			default:
				return "";
		}
	}
    
    /**
     * 近一周日期List
     * 
     * @return
     */
    public static List<String> getNearlyWeek() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar begin = Calendar.getInstance();// 得到一个Calendar的实例
        begin.setTime(new Date()); // 设置时间为当前时间
        begin.add(Calendar.DATE, -6);
        Calendar end = Calendar.getInstance();
        Long startTime = begin.getTimeInMillis();
        Long endTime = end.getTimeInMillis();
        Long oneDay = 1000 * 60 * 60 * 24L;// 一天的时间转化为ms
        List<String> dates = new ArrayList<String>();
        Long time = startTime;
        int i = 0;
        while (time <= endTime) {
            Date d = new Date(time);
            dates.add(i, df.format(d));
            i++;
            time += oneDay;
        }
        return dates;
    }
    
    /**
     * 根据日期获取当天时间戳区间
     * @param time
     * @return
     */
    public static long[] getDayTimestamps(String time) {
    	// 解析日期字符串
        LocalDate date = LocalDate.parse(time, DateTimeFormatter.ISO_DATE);
        
        // 获取当天的开始时间（00:00:00）
        LocalDateTime startOfDay = date.atStartOfDay();
        long startTimestamp = startOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        
        // 获取当天的结束时间（23:59:59.999）
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        long endTimestamp = endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        
        return new long[]{startTimestamp, endTimestamp};
    }
}
