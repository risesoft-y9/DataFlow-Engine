package net.risesoft.api.utils;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * @Description :
 * @ClassName JsonUtils
 * @Author lb
 * @Date 2022/8/10 17:40
 * @Version 1.0
 */
public class JsonUtils {

    /**
     * @param jsonObject
     * @return
     * @throws
     * @Title: json2propMap
     * @Description: 解析json 转换 Map
     */
    public static Map<String, Object> json2propMap(JSONObject jsonObject) {
        String tmpKey = "";
        String tmpKeyPre = "";
        Map<String, Object> configMap = new HashMap<String, Object>();
        json2prop(jsonObject, tmpKey, tmpKeyPre, configMap);
        return configMap;
    }

    /**
     * @param jsonObject
     * @param tmpKey
     * @param tmpKeyPre
     * @param configMap
     * @throws
     * @Title: json2prop
     * @Description: 递归解析
     */
    private static void json2prop(JSONObject jsonObject, String tmpKey, String tmpKeyPre, Map<String, Object> configMap) {
        Iterator<String> iterator = jsonObject.keySet().iterator();
        while (iterator.hasNext()) {
            // 获得key
            String key = iterator.next();
            String value = jsonObject.getString(key);
            Object valueObject = null;
            try {
                valueObject = JSONObject.parse(value);
            } catch (Exception e) {
                // 如果解析出错，就说明已经到头了，放入map然后继续解析
                configMap.put(tmpKey + key, value);
                continue;
            }
            // 如果是集合，需要特殊解析
            if (valueObject instanceof Collection<?>) {
                List<?> list = (List<?>) valueObject;
                tmpKeyPre = tmpKey;
                for (int i = 0; i < list.size(); i++) {
                    String itemKey = tmpKey + key + "[" + i + "]";
                    Object o = list.get(i);
                    if (o instanceof JSONObject) {
                        itemKey += ".";
                        JSONObject itemValue = (JSONObject) list.get(i);
                        json2prop(itemValue, itemKey, tmpKeyPre, configMap);
                    } else {
                        configMap.put(itemKey,o);
                    }


                }
            } else if (valueObject instanceof JSONObject) {
                JSONObject jsonStr = JSONObject.parseObject(value);
                tmpKeyPre = tmpKey;
                tmpKey += key + ".";
                json2prop(jsonStr, tmpKey, tmpKeyPre, configMap);
                tmpKey = tmpKeyPre;
            } else {
                configMap.put(tmpKey + key, value);
                continue;
            }
        }
    }


}
