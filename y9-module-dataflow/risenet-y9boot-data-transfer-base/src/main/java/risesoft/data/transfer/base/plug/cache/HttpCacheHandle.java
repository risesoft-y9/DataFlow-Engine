package risesoft.data.transfer.base.plug.cache;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import risesoft.data.transfer.core.exception.FrameworkErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.handle.cache.CacheHandle;

/**
 * http实现的缓存组件 需配置serverPath为服务器地址 缓存服务器需实现接口为 serverPath+"/cache"
 * 接收值:{operation:'get,put,push,poll,remove',parameter:"{ json根据operation不同返回不同
 * }"} 
 * 返回值 { data:"", success:true/false,是否成功调用 msg:"",异常时候的信息 } 
 * get:  根据key获取一个唯一的参数 
 *      接收参数: key 
 *      返回值: value 
 * put :set一个key和value 
 *       接收参数:key,value 
 *       返回值: 无
 * push :追加值到队列中 
 *       接收参数:key:队列key,value 
 *         返回值: 队列的数量 
 * poll :将队列中的值获取到获取完成后移除对应队列的值
 *       接收参数:key:队列key,size:最大获取量 
 *       返回值: list 对应key的值 
 * remove: 删除一个key 
 *       接收参数：key 
 *       返回值 boolean 是否删除到对应的key
 * 
 * 所有的请求方式均为post
 * 
 * @typeName HttpCacheHandle
 * @date 2025年8月20日
 * @author lb
 */
public class HttpCacheHandle implements CacheHandle {

	/**
	 * 服务器地址
	 */
	private String serverPath;

	public HttpCacheHandle(@ConfigParameter(required = true, description = "服务器地址") String serverPath) {
		this.serverPath = serverPath;
	}

	@Override
	public <T> List<T> poll(String key, int size, Class<T> reClass) {
		return send("poll", createMap("key", key, "size", size)).getJSONArray("data").toJavaList(reClass);
	}

	@Override
	public boolean remove(String key) {
		return send("remove", createMap("key", key)).getBooleanValue("data");
	}

	@Override
	public <T> T get(String key, Class<T> returnClass) {
		return send("get", createMap("key", key)).getObject("data", returnClass);
	}

	@Override
	public void put(String key, Object value) {
		send("put", createMap("key", key, "value", value));
	}

	@Override
	public int push(String key, Object value) {
		return send("push", createMap("key", key, "value", value)).getIntValue("data");
	}

	private Map<String, Object> createMap(String key, Object value) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		return map;
	}

	private Map<String, Object> createMap(String key, Object value, String key2, Object value2) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		map.put(key2, value2);
		return map;
	}

	private JSONObject send(String operation, Map<String, Object> parameter) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("operation", operation);
		request.put("parameter", parameter);
		try {
			JSONObject jsonObject = JSON.parseObject(post(serverPath, JSON.toJSONString(request)));
			if (jsonObject.getBooleanValue("success")) {
				return jsonObject;
			} else {
				new RuntimeException(jsonObject.getString("msg"));
			}

		} catch (Exception e) {
			throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "缓存组件处理请求失败!", e);
		}
		return null;
	}

	private static String post(String url, String json) throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(json)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}
}
