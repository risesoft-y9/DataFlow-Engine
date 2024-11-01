package net.risedata.rpc.consumer.utils;

import io.netty.util.internal.StringUtil;
import net.risedata.rpc.consumer.annotation.RPCClient;
import net.risedata.rpc.consumer.config.ConsumerApplication;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 一些统一的操作攻击类
 * @Author lb176
 * @Date 2021/4/29==15:19
 */
public class ConsumerUtils {
    private static final Pattern URL_MATHS = Pattern.compile("\\$\\{(.*)}");

    public static String getManagerName(Class<?> type, RPCClient rpcClient) {
        return StringUtils.isEmpty(rpcClient.managerName()) ? type.getName() + "Manager" : ConsumerUtils.getValue(rpcClient.managerName());
    }

    public static String getValue(String str) {
        Matcher matcher = URL_MATHS.matcher(str);
        if (matcher.find()) {
            String m = matcher.group(0);
            String m2 = m.substring(2, m.length() - 1);
            String[] kvs = m2.split(":", 2);
            String k = ConsumerApplication.propertyResolver.getProperty(kvs[0]);
            if (StringUtil.isNullOrEmpty(k)) {
                if (kvs.length < 2) {
                    throw new NullPointerException(m2 + " key:" + kvs[0] + " is null");
                }
                k = kvs[1];
            }
            return str.replace(m, k);
        }
        return str;
    }

}
