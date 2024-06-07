package net.risesoft.api.utils;

import com.alibaba.fastjson.JSONObject;

import net.risesoft.api.persistence.model.config.Config;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

/**
 * @author gyn
 */

public class PropertiesUtil {
    private final static String PATH = System.getProperty("user.dir");



    public static Map<String, String> stringToMap(String content) throws Exception {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream("ss:':'".getBytes()));
        Map<String, String> map = new HashMap<String, String>((Map) properties);
        return map;
    }
    public  static Map<String, Object> getMap(Config config) throws Exception {
        String content = config.getContent();
        switch (config.getType()) {
            case Config.YAML_TYPE:
                Yaml yaml = new Yaml();
                Map<String, Object> testMap = yaml.load(content);
                return JsonUtils.json2propMap(JSONObject.parseObject(JSONObject.toJSONString(testMap)));
            case Config.JSON_TYPE: {
                return JSONObject.parseObject(content);
            }
            case Config.PROPERTIES_TYPE:
                PropertiesUtil.stringToMap(content);
                break;
            default:
                break;
        }
        return null;
    }
    public static Map<String, String> getMap(String path) {
        Properties properties = new Properties();
        properties = readFile(PATH + path);
        Map<String, String> map = new HashMap<String, String>((Map) properties);
        return map;
    }

    public static Properties readFile(String path) {
        File file = new File(path);
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

}
