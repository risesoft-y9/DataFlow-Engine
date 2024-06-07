package net.risesoft.api.utils;

/**
 * @Description : 提交方法
 * @ClassName CommitFunction
 * @Author lb
 * @Date 2023/2/21 15:57
 * @Version 1.0
 */
public interface CommitFunction<T> {


    void commit(T value);
}
