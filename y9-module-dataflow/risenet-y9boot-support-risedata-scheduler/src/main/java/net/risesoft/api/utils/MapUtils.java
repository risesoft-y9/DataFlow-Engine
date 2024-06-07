package net.risesoft.api.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description :
 * @ClassName MapUtils
 * @Author lb
 * @Date 2022/8/25 9:47
 * @Version 1.0
 */
public class MapUtils {

    public static <T> Map<String, List<T>> createMaps(List<T> lists, GetString<T> getStr) {
        String key = null;
        Map<String, List<T>> res = new HashMap<>();
        List<T> ts = null;
        for (T t : lists) {
            key = getStr.get(t);
            ts = res.get(key);
            if (ts == null) {
                ts = new ArrayList<>();
                res.put(key, ts);
            }
            ts.add(t);
        }
        return res;

    }

}
