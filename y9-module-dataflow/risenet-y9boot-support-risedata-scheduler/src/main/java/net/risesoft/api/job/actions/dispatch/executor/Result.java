package net.risesoft.api.job.actions.dispatch.executor;

/**
 * @Description : 返回值
 * @ClassName Result
 * @Author lb
 * @Date 2022/9/15 11:32
 * @Version 1.0
 */
public interface Result {
    /**
     * 成功
     * @param success
     * @return
     */
    Result onSuccess(ResultSuccess success);

    /**
     * 异常
     * @param error
     * @return
     */
    Result onError(ResultError error);

    /**
     * 同步的获取结果
     * @return
     */
    Object getValue();



}
