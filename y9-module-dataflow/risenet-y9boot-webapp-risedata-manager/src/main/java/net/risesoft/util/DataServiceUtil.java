package net.risesoft.util;

import java.util.ArrayList;
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
}
