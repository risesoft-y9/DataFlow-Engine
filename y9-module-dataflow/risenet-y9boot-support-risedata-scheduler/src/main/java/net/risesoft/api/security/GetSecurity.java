package net.risesoft.api.security;

/**
 * @Description : 获取对应的字符串
 * @ClassName GetString
 * @Author lb
 * @Date 2022/8/9 9:29
 * @Version 1.0
 */
public interface GetSecurity<T> {
    String[] getString(T t);

    /**
     * 返回权限
     * @return
     */
    int getJurisdiction(T t);
}
