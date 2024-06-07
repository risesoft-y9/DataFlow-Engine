package risesoft.data.transfer.stream.es.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LMap {
	public static final Map<String, Object> okMap = new HashMap<>();
	static ThreadLocal<Map<String, Object>> threadMap = new ThreadLocal<Map<String, Object>>() {
		protected Map<String, Object> initialValue() {
			HashMap< String, Object> map = new HashMap<>();
			map.put("status", 200);
			return map;
		};
	};
	static {
		okMap.put("data", "ok");
		okMap.put("status", 200);
		
	}
	/**
	 * key转为小写
	 * @param map
	 */
	public static void keyToLower(Map<String, Object> map) {
		map.keySet().forEach((String key)->{
		    map.put(key.toLowerCase(), map.get(key));	
		});
	}
	
	public static Map<String, Object> toMap(Object[]... args) {
		Map<String, Object> map = new HashMap<String, Object>();
        for (Object[] objects : args) {
			map.put((String)objects[0], objects[1]);
		}
		return map;
	}
   
	public static Map<String, Object> toMap(Object... args) {
		Map<String, Object> map = new HashMap<String, Object>();    
        for (int i = 0; i < args.length; i++) {
        	Object[] o = new Object[2];
        	if (args[i] instanceof Object[]) {
				Object[] os = (Object[]) args[i];
				for (int j = 0; j < os.length; j++) {
					o = new Object[]{os[j],os[j+1]};		
					j++;
					map.put((String)o[0], o[1]);
				}
			}else {				
				o = new Object[]{args[i],args[i+1]};		
				i++;
			}
		    map.put((String)o[0], o[1]);
        }
		return map;
	}
	
	public static Object[] to(Object objects,Object value) {
		return new Object[] {objects,value};
	}
	
	public static Map<String, Object> toMap(String key1,Object value1) {
		return toMap(to(key1, value1));
	}
	public static Map<String, Object> toMap(String key1,Object value1,String key2,Object value2) {
		return toMap(to(key1, value1),to(key2, value2));
	}
	public static Map<String, Object> toMap(String key1,Object value1,String key2,Object value2,String key3,Object value3) {
		return toMap(to(key1, value1),to(key2, value2),to(key3, value3));
	}
	public static Map<String, Object> toMap(String key1,Object value1,String key2,Object value2,String key3,Object value3,String key4,Object value4) {
		return toMap(to(key1, value1),to(key2, value2),to(key3, value3),to(key4, value4));
	}
	
	public static Map<String, Object> to200(Object...objects) {
		
		if (objects!=null) {   
				return toMap("status",200,objects);
		}
		return toMap(to("status",200));
	}
	
/**
 * 注意 一般用于http返回此对象对于线程来说是单利的
 * @param data
 * @return
 */
	public static Map<String, Object> toData(Object data) {
	    Map<String, Object> m =threadMap.get();
	    m.put("data", data);
	    return m;
	}
	
	public static Map<String, Object> toOk(){
		return okMap;
	}
	
	public static Map<String, Object> to500(String msg,Object data){
		return toMap(to("status",500),to(msg, data));
	}
	
	public  static <K,V> MapPack<K,V> createMap(K k,V v) {
		
		return new MapPack<K,V>(k,v);
	}
	public static <V> MapPack<String,V> createMap(Class<V> v){
		return new MapPack<String, V>();
	}
	
    @SuppressWarnings("unchecked")
	public static <K,V> Map<K, Map<K,V>> listToMap(Object key,List<Map<K, V>> maps){
		Map<K, Map<K,V>> returnMap = new HashMap<>();
		for (Map<K, V> map : maps) {
            returnMap.put((K) map.get(key), map);
		}
		return returnMap;
    	
    }
} 
