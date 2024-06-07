package net.risesoft.api.job.actions.dispatch.executor;

/**
 * @Description : 成功返回值
 * @ClassName Result
 * @Author lb
 * @Date 2022/9/15 11:32
 * @Version 1.0
 */
public interface ResultSuccess {
    /**
     * 成功
     * @param value
     * @return
     */
    void onSuccess(Object value);



}
