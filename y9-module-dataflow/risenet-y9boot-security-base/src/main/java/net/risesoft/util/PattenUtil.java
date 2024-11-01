package net.risesoft.util;

import org.springframework.util.PatternMatchUtils;

import java.util.List;

/**
 * @Description :
 * @ClassName PattenUtils
 * @Author lb
 * @Date 2022/8/9 15:21
 * @Version 1.0
 */
public class PattenUtil {


    public static boolean hasMatch(String source, String compare) {
        return PatternMatchUtils.simpleMatch(source, compare);
    }


    public static boolean hasMatch(List<String> source, String compare) {
    	if (source==null) {
			return false;
		}
        for (String s : source) {
            if (hasMatch(s, compare)) {
                return true;
            }
        }
        return false;
    }
    public static boolean hasMatch(String[] source, String compare) {
        for (String s : source) {
            if (hasMatch(s, compare)) {
                return true;
            }
        }
        return false;
    }
}
